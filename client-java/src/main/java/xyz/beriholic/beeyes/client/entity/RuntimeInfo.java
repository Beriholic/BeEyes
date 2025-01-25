package xyz.beriholic.beeyes.client.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RuntimeInfo {
    long timestamp;
    double cpuUsage;
    double memoryUsage;
    double diskUsage;
    double networkUploadSpeed;
    double networkDownloadSpeed;
    double diskWriteSpeed;
    double diskReadSpeed;
}
