package xyz.beriholic.beeyes.entity.vo.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MachineInfoVO {
    private SystemInfo systemInfo;
    private CpuInfo cpuInfo;
    private MemoryInfo memoryInfo;
    private List<NetworkInterfaceInfo> networkInterfaceInfo;
    private DiskInfo diskInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SystemInfo {
        private String osName;
        private String kernelVersion;
        private String osVersion;
        private String cpuArch;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CpuInfo {
        private String name;
        private Integer coreCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MemoryInfo {
        private Double totalMemory;
        private Double totalSwap;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class NetworkInterfaceInfo {
        private String name;
        private List<String> ipv4 = new ArrayList<>();
        private List<String> ipv6 = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DiskInfo {
        private double total;
    }
}