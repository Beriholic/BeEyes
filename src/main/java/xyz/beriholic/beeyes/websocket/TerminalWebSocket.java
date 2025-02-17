package xyz.beriholic.beeyes.websocket;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson2.JSON;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.dto.ClientSSH;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoReportVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientSSHMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@ServerEndpoint("/terminal/{clientId}/{token}")
public class TerminalWebSocket {
    private final static Map<Session, Shell> sessionMap = new ConcurrentHashMap<>();
    private static ClientDetailMapper detailMapper;
    private static ClientSSHMapper clientSSHMapper;
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    @Resource
    public void setClientDetailMapper(ClientDetailMapper clientDetailMapper) {
        TerminalWebSocket.detailMapper = clientDetailMapper;
    }

    @Resource
    public void setClientSSHMapper(ClientSSHMapper clientSSHMapper) {
        TerminalWebSocket.clientSSHMapper = clientSSHMapper;
    }

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("clientId") long clientId,
            @PathParam("token") String token
    ) throws IOException {
        Object id = StpUtil.getLoginIdByToken(token);
        if (token.isBlank() || Objects.isNull(id)) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "未登陆或登陆已过期"));
            return;
        }

        ClientDetail clientDetail = detailMapper.selectById(clientId);
        if (Objects.isNull(clientDetail)) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "主机未激活或者不存在"));
            return;
        }
        ClientSSH ssh = clientSSHMapper.selectById(clientDetail.getId());
        if (Objects.isNull(ssh)) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "主机 SSH 连接信息不存在"));
            return;
        }

        MachineInfoReportVO.NetworkInterfaceInfo networkInterfaceInfo = JSON.parseObject(
                clientDetail.getNetworkInterfaceInfoJSON(),
                MachineInfoReportVO.NetworkInterfaceInfo.class
        );

        for (String ipv4 : networkInterfaceInfo.getIpv4()) {
            if (this.createSSHConnection(session, ssh, ipv4)) {
                log.info("主机 {} SSH连接创建成功[ipv4]", ipv4);
                return;
            }
        }

        for (String ipv6 : networkInterfaceInfo.getIpv6()) {
            if (this.createSSHConnection(session, ssh, ipv6)) {
                log.info("主机 {} SSH连接创建成功[ipv6]", ipv6);
                return;
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        Shell shell = sessionMap.get(session);
        OutputStream outputStream = shell.outputStream;
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        Shell shell = sessionMap.get(session);
        if (Objects.nonNull(shell)) {
            shell.close();
            sessionMap.remove(session);
            log.info("主机 {} SSH 连接已断开", shell.jsession.getHost());
        }
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        log.error("WebSocket 连接出错: ", error);
        session.close();
    }


    private boolean createSSHConnection(Session session, ClientSSH ssh, String ip) throws IOException {
        JSch jSch = new JSch();
        try {
            com.jcraft.jsch.Session jsession = jSch.getSession(ssh.getUsername(), ip, ssh.getPort());
            jsession.setPassword(ssh.getPassword());
            jsession.setConfig("StrictHostKeyChecking", "no");
            jsession.setTimeout(3000);
            jsession.connect();
            ChannelShell channel = (ChannelShell) jsession.openChannel("shell");
            channel.setPtyType("xterm");
            channel.connect(1000);
            sessionMap.put(session, new Shell(session, jsession, channel));
            return true;
        } catch (JSchException e) {
            String message = e.getMessage();
            if (message.equals("Auth fail")) {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "SSH 连接失败，用户名或密码错误"));
                log.error("SSH 连接失败，用户名或密码错误");
            } else if (message.equals("Connection refused")) {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "拒绝连接，请检查 SSH 端口是否开放"));
                log.error("拒绝连接，请检查 SSH 端口是否开放");
            } else {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, message));
                log.error("SSH 连接出错: ", e);
            }
        }
        return false;
    }

    private class Shell {
        public final Session session;
        public final com.jcraft.jsch.Session jsession;
        public final ChannelShell channel;
        public final InputStream inputStream;
        public final OutputStream outputStream;

        public Shell(Session session, com.jcraft.jsch.Session jsession, ChannelShell channel) throws IOException {
            this.session = session;
            this.jsession = jsession;
            this.channel = channel;
            this.inputStream = channel.getInputStream();
            this.outputStream = channel.getOutputStream();
            service.submit(this::read);
        }

        public void read() {
            try {
                byte[] buffer = new byte[1024 * 1024];
                int i;
                while ((i = inputStream.read(buffer)) != -1) {
                    String text = new String(Arrays.copyOfRange(buffer, 0, i), StandardCharsets.UTF_8);
                    session.getBasicRemote().sendText(text);
                }
            } catch (Exception e) {
                log.error("读取 SSH InputStream 时出现问题: ", e);
            }
        }

        public void close() throws IOException {
            inputStream.close();
            outputStream.close();
            channel.disconnect();
            jsession.disconnect();
            service.shutdown();
        }
    }
}

