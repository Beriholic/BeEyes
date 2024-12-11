package xyz.beriholic.beeyes.entity.vo.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RuntimeInfoVO {
    @NotNull
    long timestamp;
    @NotNull
    double cpuUsage;
    @NotNull
    double memoryUsage;
    @NotNull
    double diskUsage;
    @NotNull
    double networkUploadSpeed;
    @NotNull
    double networkDownloadSpeed;
    @NotNull
    double diskWriteSpeed;
    @NotNull
    double diskReadSpeed;
}
