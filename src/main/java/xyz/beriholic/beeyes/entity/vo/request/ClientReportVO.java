package xyz.beriholic.beeyes.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import xyz.beriholic.beeyes.entity.dto.NetworkInterfaceInfo;

@Data
public class ClientReportVO {
    @NotNull
    String osArch;
    @NotNull
    String osName;
    @NotNull
    String osVersion;
    @NotNull
    int osBitSize;
    @NotNull
    String cpuName;
    int cpuCoreCount;
    double memorySize;
    double diskSize;
    NetworkInterfaceInfo networkInterfaceInfo;
}
