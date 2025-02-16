package xyz.beriholic.beeyes.entity.vo.response;

import lombok.Data;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfo;

@Data
public class RuntimeInfoCurrentVO {
    Boolean online;
    Long clientId;
    Long timestamp;
    Double cpuUsage;
    Double memoryUsage;
    Double swapUsage;
    Double diskUsage;
    Double networkUploadSpeed;
    Double networkDownloadSpeed;

    public void from(RuntimeInfo runtimeInfo) {
        this.clientId = runtimeInfo.getClientId();
        this.timestamp = runtimeInfo.getTimestamp();
        this.cpuUsage = runtimeInfo.getCpuUsage();
        this.memoryUsage = runtimeInfo.getMemoryUsage();
        this.swapUsage = runtimeInfo.getSwapUsage();
        this.diskUsage = runtimeInfo.getDiskUsage();
        this.networkUploadSpeed = runtimeInfo.getNetworkUploadSpeed();
        this.networkDownloadSpeed = runtimeInfo.getNetworkDownloadSpeed();
    }
}
