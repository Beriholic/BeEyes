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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@Slf4j
@Component
@ServerEndpoint("/terminal/{machineId}/{token}")
public class TerminalWebSocket {
    private final static Map<Session, Shell> sessionMap = Maps.newConcurrentMap();
    private static MachineService machineService;
    private static ThreadPoolTaskExecutor executor;

    @Autowired
    @Qualifier("ioIntensiveExecutor")
    public void setExecutor(ThreadPoolTaskExecutor executor) {
        TerminalWebSocket.executor = executor;
    }

    @Resource
    public void setMachineService(MachineService machineService) {
        TerminalWebSocket.machineService = machineService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("machineId") long machineId, @PathParam("token") String token) throws IOException {
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

        boolean connected = false;

        for (String ipv4 : machineSSHInfoView.getIpv4()) {
            String ip = ipv4.contains("/") ? ipv4.split("/")[0] : ipv4;
            if (this.createSSHConnection(session, machineSSHInfoView, ip)) {
                connected = true;
                log.info("主机 {} SSH连接创建成功[ipv4={}]", machineId, ip);
                break;
            }
        }

        if (!connected) {
            for (String ipv6 : machineSSHInfoView.getIpv6()) {
                String ip = ipv6.contains("/") ? ipv6.split("/")[0] : ipv6;
                if (this.createSSHConnection(session, machineSSHInfoView, ip)) {
                    connected = true;
                    log.info("主机 {} SSH连接创建成功[ipv6={}]", machineId, ip);
                    break;
                }
            }
        }

        if (connected) {
            executor.submit(() -> machineService.updateLastConnectTime(machineId, userId));
        } else {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "无法连接到主机，请检查网络或配置"));
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        Shell shell = sessionMap.get(session);
        if (shell != null && shell.channel.isConnected()) {
            OutputStream outputStream = shell.outputStream;
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
    }

    @OnClose
    public void onClose(Session session) {
        Shell shell = sessionMap.remove(session);
        if (shell != null) {
            shell.close();
            log.info("主机 SSH 连接已断开");
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 连接出错: ", error);
        onClose(session);
    }

    private boolean createSSHConnection(Session session, MachineSSHInfoView view, String ip) throws IOException {
        JSch jSch = new JSch();
        try {
            com.jcraft.jsch.Session jsession = jSch.getSession(view.getName(), ip, view.getPort());
            Properties config = new Properties();

            config.put("StrictHostKeyChecking", "no");

            jsession.setPassword(view.getPassword());
            jsession.setConfig(config);

            jsession.setTimeout(10000);

            jsession.connect();

            ChannelShell channel = (ChannelShell) jsession.openChannel("shell");
            channel.setPtyType("xterm");
            channel.connect(5000);

            sessionMap.put(session, new Shell(session, jsession, channel));
            return true;
        } catch (JSchException e) {
            log.warn("尝试连接 {} 失败: {}", ip, e.getMessage());
        }
        return false;
    }

    private static class Shell {
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

            executor.submit(this::read);
        }

        public void read() {
            byte[] buffer = new byte[1024];
            int i;
            try {
                while ((i = inputStream.read(buffer)) != -1) {
                    synchronized (session) {
                        session.getBasicRemote().sendText(new String(Arrays.copyOfRange(buffer, 0, i), StandardCharsets.UTF_8));
                    }
                }
            } catch (Exception e) {
                if (!"Socket closed".equals(e.getMessage())) {
                    log.error("读取 SSH InputStream 时出现问题: ", e);
                }
            } finally {
                this.close();
            }
        }

        public void close() {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (channel != null) channel.disconnect();
                if (jsession != null) jsession.disconnect();
            } catch (IOException e) {
                log.error("关闭资源出错", e);
            }
        }
    }
}