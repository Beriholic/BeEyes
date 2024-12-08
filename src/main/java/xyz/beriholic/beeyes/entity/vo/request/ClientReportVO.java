package xyz.beriholic.beeyes.entity.vo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import xyz.beriholic.beeyes.entity.dto.InterfaceInfo;

import java.util.List;

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
    @NotNull
    List<InterfaceInfo> interfacesInfo;
}
