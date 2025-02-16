package xyz.beriholic.beeyes.entity.vo.response;

import lombok.Data;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.dto.Machine;

@Data
public class MachineInfoVO {
    String name;
    String location;
    String osName;
    String kernelVersion;
    String osVersion;
    String cpuArch;
    String cpuName;
    Integer cpuCoreCount;
    Double totalMemory;
    Double totalSwap;
    Double totalDiskSize;
    boolean active;

    public void copyFrom(Machine machine) {
        this.name = machine.getName();
        this.location = machine.getLocation();
    }

    public void copyFrom(ClientDetail clientDetail) {
        this.osName = clientDetail.getOsName();
        this.kernelVersion = clientDetail.getKernelVersion();
        this.osVersion = clientDetail.getOsVersion();
        this.cpuArch = clientDetail.getCpuArch();
        this.cpuName = clientDetail.getCpuName();
        this.cpuCoreCount = clientDetail.getCpuCoreCount();
        this.totalMemory = clientDetail.getTotalMemory();
        this.totalSwap = clientDetail.getTotalSwap();
        this.totalDiskSize = clientDetail.getTotalDiskSize();
    }
}
