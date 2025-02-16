package xyz.beriholic.beeyes.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;

import java.time.Instant;
import java.util.Date;

@Measurement(name = "runtime_info")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuntimeInfoDB {
    @Column(tag = true)
    Long clientId;
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

    public static RuntimeInfoDB from(long clientId, RuntimeInfoVO vo) {
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
