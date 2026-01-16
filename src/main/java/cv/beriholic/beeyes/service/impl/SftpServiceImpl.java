package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.models.entity.dto.MachineSSHInfoView;
import cv.beriholic.beeyes.service.SftpService;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * SFTP file transfer service implementation using JSch.
 */
@Service
@Slf4j
public class SftpServiceImpl implements SftpService {

    private static final int TIMEOUT_MS = 10000;

    @Override
    public List<FileEntry> listFiles(MachineSSHInfoView sshInfo, String path) {
        ChannelSftp sftp = null;
        try {
            sftp = createSftpChannel(sshInfo);
            Vector<ChannelSftp.LsEntry> entries = sftp.ls(path);

            List<FileEntry> result = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            for (ChannelSftp.LsEntry entry : entries) {
                String name = entry.getFilename();
                // Skip . and ..
                if (".".equals(name) || "..".equals(name)) {
                    continue;
                }

                String entryPath = path.endsWith("/") ? path + name : path + "/" + name;
                boolean isDir = entry.getAttrs().isDir();
                long size = entry.getAttrs().getSize();
                String modTime = dateFormat.format(new Date(entry.getAttrs().getMTime() * 1000L));

                result.add(new FileEntry(name, entryPath, isDir, size, modTime));
            }

            // Sort: directories first, then by name
            result.sort((a, b) -> {
                if (a.isDirectory() != b.isDirectory()) {
                    return a.isDirectory() ? -1 : 1;
                }
                return a.name().compareToIgnoreCase(b.name());
            });

            return result;
        } catch (SftpException e) {
            log.error("Failed to list files at {}: {}", path, e.getMessage());
            throw new SftpOperationException("Failed to list files: " + e.getMessage(), e);
        } finally {
            if (sftp != null) {
                disconnect(sftp);
            }
        }
    }

    @Override
    public InputStream downloadFile(MachineSSHInfoView sshInfo, String path) {
        ChannelSftp sftp = null;
        try {
            sftp = createSftpChannel(sshInfo);
            return sftp.get(path);
        } catch (SftpException e) {
            log.error("Failed to download file {}: {}", path, e.getMessage());
            throw new SftpOperationException("Failed to download file: " + e.getMessage(), e);
        } finally {
            if (sftp != null) {
                // Don't disconnect here - caller is responsible for closing the stream
                // The stream will be closed when the response is fully read
                // We need a different approach for proper cleanup
                final ChannelSftp channelToDisconnect = sftp;
                Thread cleanupThread = new Thread(() -> {
                    try {
                        Thread.sleep(30000); // Wait for stream to be consumed
                    } catch (InterruptedException ignored) {
                    }
                    disconnect(channelToDisconnect);
                });
                cleanupThread.setDaemon(true);
                cleanupThread.start();
            }
        }
    }

    @Override
    public void uploadFile(MachineSSHInfoView sshInfo, String remotePath, InputStream inputStream, String fileName) {
        ChannelSftp sftp = null;
        try {
            sftp = createSftpChannel(sshInfo);

            // Ensure directory exists
            String parentDir = remotePath.endsWith("/") ? remotePath : remotePath + "/";
            String targetPath = parentDir + fileName;

            sftp.put(inputStream, targetPath);
            log.info("File uploaded successfully: {}", targetPath);
        } catch (SftpException e) {
            log.error("Failed to upload file to {}: {}", remotePath, e.getMessage());
            throw new SftpOperationException("Failed to upload file: " + e.getMessage(), e);
        } finally {
            if (sftp != null) {
                disconnect(sftp);
            }
        }
    }

    @Override
    public void deleteFile(MachineSSHInfoView sshInfo, String path) {
        ChannelSftp sftp = null;
        try {
            sftp = createSftpChannel(sshInfo);
            deletePath(sftp, path);
            log.info("File deleted successfully: {}", path);
        } catch (SftpException e) {
            log.error("Failed to delete {}: {}", path, e.getMessage());
            throw new SftpOperationException("Failed to delete: " + e.getMessage(), e);
        } finally {
            if (sftp != null) {
                disconnect(sftp);
            }
        }
    }

    private void deletePath(ChannelSftp sftp, String path) throws SftpException {
        // Check if it's a directory using lstat
        SftpATTRS attrs;
        try {
            attrs = sftp.lstat(path);
        } catch (SftpException e) {
            throw new SftpException(e.id, "Path not found: " + path);
        }

        if (attrs.isDir()) {
            // It's a directory - delete contents first, then the directory
            Vector<ChannelSftp.LsEntry> entries = sftp.ls(path);
            for (ChannelSftp.LsEntry item : entries) {
                String name = item.getFilename();
                if (".".equals(name) || "..".equals(name)) {
                    continue;
                }
                String itemPath = path.endsWith("/") ? path + name : path + "/" + name;
                deletePath(sftp, itemPath);
            }
            // Now remove the empty directory
            sftp.rmdir(path);
        } else {
            // It's a file - just delete it
            sftp.rm(path);
        }
    }

    @Override
    public void createDirectory(MachineSSHInfoView sshInfo, String path) {
        ChannelSftp sftp = null;
        try {
            sftp = createSftpChannel(sshInfo);
            sftp.mkdir(path);
            log.info("Directory created successfully: {}", path);
        } catch (SftpException e) {
            log.error("Failed to create directory {}: {}", path, e.getMessage());
            throw new SftpOperationException("Failed to create directory: " + e.getMessage(), e);
        } finally {
            if (sftp != null) {
                disconnect(sftp);
            }
        }
    }

    private ChannelSftp createSftpChannel(MachineSSHInfoView sshInfo) {
        try {
            // Try all IPs until one works
            String connectedIp = null;

            // Try IPv4 first
            for (String ipv4 : sshInfo.getIpv4()) {
                String ip = ipv4.contains("/") ? ipv4.split("/")[0] : ipv4;
                if (tryConnect(sshInfo, ip)) {
                    connectedIp = ip;
                    log.info("SFTP connected via IPv4: {}", ip);
                    break;
                }
            }

            // If no IPv4 worked, try IPv6
            if (connectedIp == null) {
                for (String ipv6 : sshInfo.getIpv6()) {
                    String ip = ipv6.contains("/") ? ipv6.split("/")[0] : ipv6;
                    if (tryConnect(sshInfo, ip)) {
                        connectedIp = ip;
                        log.info("SFTP connected via IPv6: {}", ip);
                        break;
                    }
                }
            }

            if (connectedIp == null) {
                throw new SftpOperationException("无法连接到服务器，请检查服务器是否在线", null);
            }

            // Create session and open SFTP channel
            JSch jSch = new JSch();
            Session session = jSch.getSession(sshInfo.getName(), connectedIp, sshInfo.getPort());
            session.setPassword(sshInfo.getPassword());

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(TIMEOUT_MS);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect(TIMEOUT_MS);

            return (ChannelSftp) channel;
        } catch (SftpOperationException e) {
            throw e;
        } catch (Exception e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("No route to host")) {
                throw new SftpOperationException("无法连接到服务器，请检查网络或服务器是否在线", e);
            } else if (msg != null && msg.contains("Connection refused")) {
                throw new SftpOperationException("无法连接到服务器，请检查SSH端口是否正确或服务器是否在线", e);
            } else if (msg != null && msg.contains("timeout")) {
                throw new SftpOperationException("连接超时，请检查网络状况", e);
            } else if (msg != null && msg.contains("closed by foreign host")) {
                throw new SftpOperationException("服务器拒绝连接，请检查SSH用户名和密码是否正确", e);
            } else if (msg != null && msg.contains("Auth fail")) {
                throw new SftpOperationException("SSH认证失败，请检查用户名和密码是否正确", e);
            }
            throw new SftpOperationException("SFTP连接失败: " + e.getMessage(), e);
        }
    }

    private boolean tryConnect(MachineSSHInfoView sshInfo, String ip) {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(sshInfo.getName(), ip, sshInfo.getPort());
            session.setPassword(sshInfo.getPassword());
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(TIMEOUT_MS);
            session.connect(TIMEOUT_MS);
            session.disconnect();
            return true;
        } catch (Exception e) {
            log.warn("Failed to connect to {}: {}", ip, e.getMessage());
            return false;
        }
    }

    private void disconnect(ChannelSftp sftp) {
        try {
            if (sftp != null && sftp.isConnected()) {
                Session session = sftp.getSession();
                sftp.disconnect();
                if (session != null) {
                    session.disconnect();
                }
            }
        } catch (Exception e) {
            log.warn("Error disconnecting SFTP: {}", e.getMessage());
        }
    }

    /**
     * Exception for SFTP operation failures.
     */
    public static class SftpOperationException extends RuntimeException {
        public SftpOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
