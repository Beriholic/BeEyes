package xyz.beriholic.beeyes.entity.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.AllArgsConstructor;

import java.time.Instant;

@Measurement(name = "runtime_info")
@AllArgsConstructor
public class RuntimeInfo {
    @Column(tag = true)
    int clientId;
    @Column(timestamp = true)
    Instant timestamp;
    @Column
    double cpuUsage;
    @Column
    double memoryUsage;
    @Column
    double diskUsage;
    @Column
    double networkUploadSpeed;
    @Column
    double networkDownloadSpeed;
    @Column
    double diskWriteSpeed;
    @Column
    double diskReadSpeed;


//    public static RuntimeInfo from(int clientId, RuntimeInfoVO vo) {
//        return new RuntimeInfo(
//                clientId,
//                new Date(vo.getTimestamp()).toInstant(),
//                vo.getCpuUsage(),
//                vo.getMemoryUsage(),
//                vo.getDiskUsage(),
//                vo.getNetworkUploadSpeed(),
//                vo.getNetworkDownloadSpeed(),
//                vo.getDiskWriteSpeed(),
//                vo.getDiskReadSpeed()
//        );
//    }
}
