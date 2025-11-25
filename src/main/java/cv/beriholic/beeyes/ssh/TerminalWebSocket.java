package cv.beriholic.beeyes.ssh;

import cn.dev33.satoken.stp.StpUtil;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import cv.beriholic.beeyes.models.entity.dto.MachineSSHInfoView;
import cv.beriholic.beeyes.service.MachineService;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@ServerEndpoint("/terminal/{machineId}/{token}")
public class TerminalWebSocket {
    private final static Map<Session, Shell> sessionMap = Maps.newConcurrentMap();
    private static MachineService machineService;
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    @Resource
    public void setMachineService(MachineService machineService) {
        TerminalWebSocket.machineService = machineService;
    }

    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("machineId") long machineId,
            @PathParam("token") String token
    ) throws IOException {
        Object id = StpUtil.getLoginIdByToken(token);
        if (token.isBlank() || Objects.isNull(id)) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "未登陆或登陆已过期"));
            return;
        }
        Long userId = Long.valueOf((String) id);
        MachineSSHInfoView machineSSHInfoView = machineService.getMachineSSHInfoView(userId, machineId);
        if (Objects.isNull(machineSSHInfoView)) {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "服务器异常或未配置连接信息"));
            return;
        }

        for (String ipv4 : machineSSHInfoView.getIpv4()) {
            if (this.createSSHConnection(session, machineSSHInfoView, ipv4)) {
                service.submit(() -> machineService.updateLastConnectTime(machineId, userId));
                log.info("主机 {} SSH连接创建成功[ipv4={}]", machineId, ipv4);
            }
        }
        for (String ipv6 : machineSSHInfoView.getIpv6()) {
            if (this.createSSHConnection(session, machineSSHInfoView, ipv6)) {
                service.submit(() -> machineService.updateLastConnectTime(machineId, userId));
                log.info("主机 {} SSH连接创建成功[ipv6={}]", machineId, ipv6);
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


    private boolean createSSHConnection(Session session, MachineSSHInfoView view, String ip) throws IOException {
        JSch jSch = new JSch();
        try {
            com.jcraft.jsch.Session jsession = jSch.getSession(view.getName(), ip, view.getPort());
            jsession.setPassword(view.getPassword());
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