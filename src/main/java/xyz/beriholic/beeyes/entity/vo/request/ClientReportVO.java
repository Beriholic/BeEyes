package xyz.beriholic.beeyes.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import xyz.beriholic.beeyes.entity.dto.NetworkInterfaceInfo;

@Data
@Accessors(chain = true)
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
    double diskTotalSize;
    NetworkInterfaceInfo networkInterfaceInfo;
}
