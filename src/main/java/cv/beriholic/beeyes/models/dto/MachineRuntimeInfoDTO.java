package cv.beriholic.beeyes.models.dto;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import cv.beriholic.beeyes.models.dto.system.CPUInfo;
import cv.beriholic.beeyes.models.dto.system.DiskInfo;
import cv.beriholic.beeyes.models.dto.system.MemoryInfo;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Measurement(name = "machine_runtime_info")
@Data
public class MachineRuntimeInfoDTO {
    @Column(timestamp = true)
    private Instant timestamp;
    @Column
    private Double cpuUsage;
    @Column
    private Double memoryUsage;
    @Column
    private Double swapUsage;
    @Column
    private Double diskUsage;

    public static MachineRuntimeInfoDTO from(RuntimeInfo from) {
        CPUInfo cpuInfo = from.getCpuInfo();
        MemoryInfo memoryInfo = from.getMemoryInfo();
        List<DiskInfo> diskInfo = from.getDiskInfo();

        MachineRuntimeInfoDTO to = new MachineRuntimeInfoDTO();
        to.setTimestamp(Instant.now());
        to.setCpuUsage(cpuInfo.getUsage());
        to.setMemoryUsage(memoryInfo.getPercentMemory());
        to.setSwapUsage(memoryInfo.getPercentSwap());

        double avgDiskUsage = diskInfo.stream()
                .mapToDouble(DiskInfo::getPercent)
                .average()
                .orElse(0.0);
        to.setDiskUsage(avgDiskUsage);
        return to;
    }
}
