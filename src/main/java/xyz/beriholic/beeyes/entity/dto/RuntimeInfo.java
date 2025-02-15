package xyz.beriholic.beeyes.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;

import java.util.Date;

@AllArgsConstructor
@Data
public class RuntimeInfo {
    Long clientId;
    Long timestamp;
    Double cpuUsage;
    Double memoryUsage;
    Double swapUsage;
    Double diskUsage;
    Double networkUploadSpeed;
    Double networkDownloadSpeed;

    public static RuntimeInfo from(long clientId, RuntimeInfoVO vo) {
        return new RuntimeInfo(
                clientId,
                vo.getTimestamp(),
                vo.getCpuInfo().getUsage(),
                vo.getMemoryInfo().getPercentMemory(),
                vo.getMemoryInfo().getPercentSwap(),
                vo.getDiskInfo().getPercent(),
                vo.getNetworkInfo().getInterfaces().getFirst().getUploadSpeed(),
                vo.getNetworkInfo().getInterfaces().getFirst().getDownloadSpeed()
        );
    }

    public RuntimeInfoDB toDB() {
        return new RuntimeInfoDB(
                this.clientId,
                new Date(this.timestamp).toInstant(),
                this.cpuUsage,
                this.memoryUsage,
                this.swapUsage,
                this.diskUsage,
                this.networkUploadSpeed,
                this.networkDownloadSpeed
        );
    }
}
