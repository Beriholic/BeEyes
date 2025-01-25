package xyz.beriholic.beeyes.client.entity;

import lombok.Data;
import lombok.experimental.Accessors;

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
    double diskTotalSize;
    NetworkInterfaceInfo networkInterfaceInfo;
}

