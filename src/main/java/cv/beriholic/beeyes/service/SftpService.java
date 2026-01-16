package cv.beriholic.beeyes.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import cv.beriholic.beeyes.models.entity.dto.MachineSSHInfoView;

import java.io.InputStream;
import java.util.List;

/**
 * SFTP file transfer service.
 */
public interface SftpService {

    /**
     * Lists files in a directory on the remote server.
     *
     * @param sshInfo SSH connection info
     * @param path    remote directory path
     * @return list of file entries
     */
    List<FileEntry> listFiles(MachineSSHInfoView sshInfo, String path);

    /**
     * Downloads a file from the remote server.
     *
     * @param sshInfo SSH connection info
     * @param path    remote file path
     * @return input stream of file content
     */
    InputStream downloadFile(MachineSSHInfoView sshInfo, String path);

    /**
     * Uploads a file to the remote server.
     *
     * @param sshInfo       SSH connection info
     * @param remotePath    remote destination path
     * @param inputStream   file content
     * @param fileName      original file name
     */
    void uploadFile(MachineSSHInfoView sshInfo, String remotePath, InputStream inputStream, String fileName);

    /**
     * Deletes a file on the remote server.
     *
     * @param sshInfo SSH connection info
     * @param path    remote file path
     */
    void deleteFile(MachineSSHInfoView sshInfo, String path);

    /**
     * Creates a directory on the remote server.
     *
     * @param sshInfo SSH connection info
     * @param path    remote directory path
     */
    void createDirectory(MachineSSHInfoView sshInfo, String path);

    /**
     * File entry representation.
     */
    record FileEntry(String name, String path, @JsonProperty("directory") boolean isDirectory, long size, String modifiedTime) {
    }
}
