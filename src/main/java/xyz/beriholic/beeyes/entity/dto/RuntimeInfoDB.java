package xyz.beriholic.beeyes.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;

import java.time.Instant;
import java.util.Date;

@Measurement(name = "runtime_info")
@AllArgsConstructor
@Data
public class RuntimeInfoDB {
    @Column(tag = true)
    Integer clientId;
    @Column(timestamp = true)
    Instant timestamp;

    @Column
    Double cpuUsage;
    @Column
    Double memoryUsage;
    @Column
    Double swapUsage;
    @Column
    Double diskUsage;
    @Column
    Double networkUploadSpeed;
    @Column
    Double networkDownloadSpeed;

    public static RuntimeInfoDB from(int clientId, RuntimeInfoVO vo) {
        return new RuntimeInfoDB(
                clientId,
                new Date(vo.getTimestamp()).toInstant(),
                vo.getCpuInfo().getUsage(),
                vo.getMemoryInfo().getPercentMemory(),
                vo.getMemoryInfo().getPercentSwap(),
                vo.getDiskInfo().getPercent(),
                vo.getNetworkInfo().getInterfaces().getFirst().getUploadSpeed(),
                vo.getNetworkInfo().getInterfaces().getFirst().getDownloadSpeed()
        );
    }
}
