package cv.beriholic.beeyes.controller;

import cn.dev33.satoken.stp.StpUtil;
import cv.beriholic.beeyes.consts.PermissionCode;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.helper.PermissionValidateHelper;
import cv.beriholic.beeyes.models.dto.RestBean;
import cv.beriholic.beeyes.models.entity.dto.MachineSSHInfoView;
import cv.beriholic.beeyes.service.MachineService;
import cv.beriholic.beeyes.service.SftpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * File transfer controller for SFTP operations.
 */
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final SftpService sftpService;
    private final MachineService machineService;
    private final PermissionValidateHelper permissionValidateHelper;

    /**
     * Lists files in a remote directory.
     */
    @GetMapping("/list")
    public RestBean<List<SftpService.FileEntry>> listFiles(
            @RequestParam String serverId,
            @RequestParam(defaultValue = "/") String path) {
        if (StringUtils.isEmpty(serverId)) {
            return RestBean.failed(ErrorCode.PARAM_INVALID);
        }

        long userId = StpUtil.getLoginIdAsLong();
        permissionValidateHelper.checkPermission(userId, PermissionCode.SFTP_CONNECT);
        MachineSSHInfoView sshInfo = machineService.getMachineSSHInfoView(userId, Long.valueOf(serverId));
        if (sshInfo == null) {
            return RestBean.failed(ErrorCode.SSH_CONNECT_FAILED);
        }

        String safePath = sanitizePath(path);
        List<SftpService.FileEntry> files = sftpService.listFiles(sshInfo, safePath);
        return RestBean.success(files);
    }

    /**
     * Downloads a file from the remote server.
     */
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(
            @RequestParam String serverId,
            @RequestParam String path) throws IOException {
        if (StringUtils.isEmpty(serverId) || StringUtils.isEmpty(path)) {
            return ResponseEntity.badRequest().build();
        }

        long userId = StpUtil.getLoginIdAsLong();
        permissionValidateHelper.checkPermission(userId, PermissionCode.SFTP_CONNECT);
        MachineSSHInfoView sshInfo = machineService.getMachineSSHInfoView(userId, Long.valueOf(serverId));
        if (sshInfo == null) {
            return ResponseEntity.notFound().build();
        }

        String safePath = sanitizePath(path);
        InputStream inputStream = sftpService.downloadFile(sshInfo, safePath);

        String fileName = safePath.contains("/")
                ? safePath.substring(safePath.lastIndexOf('/') + 1)
                : safePath;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    /**
     * Uploads a file to the remote server.
     */
    @PostMapping("/upload")
    public RestBean<Void> uploadFile(
            @RequestParam String serverId,
            @RequestParam String path,
            @RequestParam(defaultValue = "/") String remoteDirectory,
            @RequestParam("file") MultipartFile file) {
        if (StringUtils.isEmpty(serverId) || StringUtils.isEmpty(remoteDirectory)) {
            return RestBean.failed(ErrorCode.PARAM_INVALID);
        }

        long userId = StpUtil.getLoginIdAsLong();
        permissionValidateHelper.checkPermission(userId, PermissionCode.SFTP_CONNECT);
        MachineSSHInfoView sshInfo = machineService.getMachineSSHInfoView(userId, Long.valueOf(serverId));
        if (sshInfo == null) {
            return RestBean.failed(ErrorCode.SSH_CONNECT_FAILED);
        }

        String safeDirectory = sanitizePath(remoteDirectory);

        try {
            sftpService.uploadFile(sshInfo, safeDirectory, file.getInputStream(), file.getOriginalFilename());
            return RestBean.success();
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return RestBean.failure(90002, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * Deletes a file on the remote server.
     */
    @DeleteMapping("/delete")
    public RestBean<Void> deleteFile(
            @RequestParam String serverId,
            @RequestParam String path) {
        if (StringUtils.isEmpty(serverId) || StringUtils.isEmpty(path)) {
            return RestBean.failed(ErrorCode.PARAM_INVALID);
        }

        long userId = StpUtil.getLoginIdAsLong();
        permissionValidateHelper.checkPermission(userId, PermissionCode.SFTP_CONNECT);
        MachineSSHInfoView sshInfo = machineService.getMachineSSHInfoView(userId, Long.valueOf(serverId));
        if (sshInfo == null) {
            return RestBean.failed(ErrorCode.SSH_CONNECT_FAILED);
        }

        String safePath = sanitizePath(path);

        try {
            sftpService.deleteFile(sshInfo, safePath);
            return RestBean.success();
        } catch (Exception e) {
            log.error("File delete failed: {}", e.getMessage());
            return RestBean.failure(90003, "文件删除失败: " + e.getMessage());
        }
    }

    /**
     * Creates a directory on the remote server.
     */
    @PostMapping("/mkdir")
    public RestBean<Void> createDirectory(
            @RequestParam String serverId,
            @RequestParam String path) {
        if (StringUtils.isEmpty(serverId) || StringUtils.isEmpty(path)) {
            return RestBean.failed(ErrorCode.PARAM_INVALID);
        }

        long userId = StpUtil.getLoginIdAsLong();
        permissionValidateHelper.checkPermission(userId, PermissionCode.SFTP_CONNECT);
        MachineSSHInfoView sshInfo = machineService.getMachineSSHInfoView(userId, Long.valueOf(serverId));
        if (sshInfo == null) {
            return RestBean.failed(ErrorCode.SSH_CONNECT_FAILED);
        }

        String safePath = sanitizePath(path);

        try {
            sftpService.createDirectory(sshInfo, safePath);
            return RestBean.success();
        } catch (Exception e) {
            log.error("Directory create failed: {}", e.getMessage());
            return RestBean.failure(90004, "目录创建失败: " + e.getMessage());
        }
    }

    /**
     * Sanitizes and validates the given path to prevent path traversal attacks.
     */
    private String sanitizePath(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        String normalized = path.replaceAll("/+", "/");
        if (normalized.contains("..")) {
            log.warn("Path traversal attempt blocked: {}", path);
            throw new SecurityException("Invalid path: path traversal not allowed");
        }
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        if (normalized.isBlank() || normalized.equals("//")) {
            return "/";
        }
        return normalized;
    }
}
