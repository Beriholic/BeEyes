package xyz.beriholic.beeyes.entity.vo.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RuntimeInfoVO {
    private Long timestamp;
    private CpuInfo cpuInfo;
    private MemoryInfo memoryInfo;
    private DiskInfo diskInfo;
    private NetworkInfo networkInfo;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CpuInfo {
        private String name;
        private Integer coreCount;
        private Double usage;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MemoryInfo {
        private Double totalMemory;
        private Double usedMemory;
        private Double freeMemory;
        private Double percentMemory;
        private Double totalSwap;
        private Double usedSwap;
        private Double freeSwap;
        private Double percentSwap;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DiskInfo {
        private Double total;
        private Double used;
        private Double free;
        private Double percent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class NetworkInfo {
        private List<NetworkInterface> interfaces = new ArrayList<>();

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class NetworkInterface {
            private String name;
            private List<String> ipv4;
            private List<String> ipv6;
            private Double uploadSpeed;
            private Double downloadSpeed;
        }
    }
}