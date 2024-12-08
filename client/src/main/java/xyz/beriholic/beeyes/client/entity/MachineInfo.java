package xyz.beriholic.beeyes.client.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class MachineInfo {
    String osArch;
    String osName;
    String osVersion;
    int osBitSize;
    String cpuName;
    int cpuCoreCount;
    double memorySize;
    double diskSize;
    List<InterfaceInfo> interfacesInfo;
}

