package cv.beriholic.beeyes.converter;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import cv.beriholic.beeyes.models.dto.system.CPUInfo;
import cv.beriholic.beeyes.models.dto.system.MachineInfo;
import cv.beriholic.beeyes.models.dto.system.MemoryInfo;
import cv.beriholic.beeyes.models.dto.system.SystemInfo;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.dto.SaveServerInput;
import cv.beriholic.beeyes.utils.DiffUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ServerMachineConverter {
    public static @NonNull SaveServerInput.TargetOf_hardware buildHardware(MachineInfo machineInfo, ServersDO serversDO) {
        SaveServerInput.TargetOf_hardware hardware = new SaveServerInput.TargetOf_hardware();
        SystemInfo systemInfo = machineInfo.getSystem_info();
        CPUInfo cpuInfo = machineInfo.getCpu_info();
        MemoryInfo memoryInfo = machineInfo.getMemory_info();

        hardware.setOsName(systemInfo.getOs_name());
        hardware.setOsVersion(String.valueOf(systemInfo.getOs_version()));
        hardware.setKernelVersion(String.valueOf(systemInfo.getKernel_version()));
        hardware.setCpuArch(systemInfo.getCpu_arch());
        hardware.setCpuCores(cpuInfo.getCore_count());
        hardware.setCpuName(cpuInfo.getName());
        hardware.setTotalMemory(memoryInfo.getTotal_memory());
        hardware.setTotalSwap(memoryInfo.getTotal_swap());

        if (Objects.nonNull(serversDO) && Objects.nonNull(serversDO.hardware())) {
            hardware.setId(Objects.requireNonNull(serversDO.hardware()).id());
            hardware.setUpdatedAt(LocalDateTime.now());
        } else {
            hardware.setId(IdUtil.getSnowflakeNextId());
            hardware.setCreatedAt(LocalDateTime.now());
            hardware.setUpdatedAt(LocalDateTime.now());
        }
        return hardware;
    }

    public static @NonNull List<SaveServerInput.TargetOf_disks> buildDisk(MachineInfo machineInfo, ServersDO serversDO) {
        return machineInfo.getDisk_info().stream().map(diskInfo -> {
            SaveServerInput.TargetOf_disks disk = new SaveServerInput.TargetOf_disks();

            disk.setDiskName(diskInfo.getName());
            disk.setFileSystem(diskInfo.getFile_system());
            disk.setDiskKind(diskInfo.getKind());
            disk.setTotalBytes(diskInfo.getTotal());

            if (Objects.isNull(serversDO) || Objects.isNull(serversDO.disks())) {
                disk.setId(IdUtil.getSnowflakeNextId());
                disk.setCreatedAt(LocalDateTime.now());
                disk.setUpdatedAt(LocalDateTime.now());
            } else {
                SaveServerInput.TargetOf_disks existingDisk = serversDO.disks().stream()
                        .filter(d -> d.diskName().equals(diskInfo.getName()))
                        .findFirst()
                        .map(SaveServerInput.TargetOf_disks::new)
                        .orElse(null);

                if (Objects.nonNull(existingDisk)) {
                    disk.setId(existingDisk.getId());
                    disk.setCreatedAt(existingDisk.getCreatedAt());
                    disk.setCreatedBy(existingDisk.getCreatedBy());

                    String newFileSystem = DiffUtils.replaceOrNotNull(existingDisk.getFileSystem(), diskInfo.getFile_system());
                    String newDiskKind = DiffUtils.replaceOrNotNull(existingDisk.getDiskKind(), diskInfo.getKind());
                    Long newTotalBytes = DiffUtils.replaceOrNotNull(existingDisk.getTotalBytes(), diskInfo.getTotal());

                    if (Objects.nonNull(newFileSystem)) {
                        disk.setFileSystem(newFileSystem);
                    }
                    if (Objects.nonNull(newDiskKind)) {
                        disk.setDiskKind(newDiskKind);
                    }
                    if (Objects.nonNull(newTotalBytes)) {
                        disk.setTotalBytes(newTotalBytes);
                    }

                    disk.setUpdatedAt(LocalDateTime.now());
                } else {
                    disk.setId(IdUtil.getSnowflakeNextId());
                    disk.setCreatedAt(LocalDateTime.now());
                    disk.setUpdatedAt(LocalDateTime.now());
                }
            }

            return disk;
        }).toList();
    }

    public static @NonNull List<SaveServerInput.TargetOf_networkInterfaces> buildNetworkInterface(MachineInfo machineInfo, ServersDO serversDO) {
        return machineInfo.getNetwork_info().getInterfaces().stream().map(networkInterface -> {
                    SaveServerInput.TargetOf_networkInterfaces network = new SaveServerInput.TargetOf_networkInterfaces();

                    network.setInterfaceName(networkInterface.getName());
                    network.setIpv4Address(networkInterface.getIpv4().toArray(new String[0]));
                    network.setIpv6Address(networkInterface.getIpv6().toArray(new String[0]));

                    if (Objects.isNull(serversDO) || Objects.isNull(serversDO.networkInterfaces())) {
                        network.setId(IdUtil.getSnowflakeNextId());
                        network.setCreatedAt(LocalDateTime.now());
                        network.setUpdatedAt(LocalDateTime.now());
                    } else {
                        SaveServerInput.TargetOf_networkInterfaces existingInterface = serversDO.networkInterfaces().stream()
                                .filter(iFace -> iFace.interfaceName().equals(networkInterface.getName()))
                                .findFirst()
                                .map(SaveServerInput.TargetOf_networkInterfaces::new)
                                .orElse(null);
                        if (Objects.nonNull(existingInterface)) {
                            network.setId(existingInterface.getId());
                            network.setUpdatedAt(LocalDateTime.now());

                            List<String> ipv4Address = DiffUtils.replaceOrNotNull(
                                    Lists.newArrayList(existingInterface.getIpv4Address()),
                                    networkInterface.getIpv4()
                            );
                            List<String> ipv6Address = DiffUtils.replaceOrNotNull(
                                    Lists.newArrayList(existingInterface.getIpv6Address()),
                                    networkInterface.getIpv6()
                            );
                            if (CollectionUtils.isNotEmpty(ipv4Address)) {
                                network.setIpv4Address(ipv4Address.toArray(new String[0]));
                            }
                            if (CollectionUtils.isNotEmpty(ipv6Address)) {
                                network.setIpv6Address(ipv6Address.toArray(new String[0]));
                            }
                        } else {
                            network.setId(IdUtil.getSnowflakeNextId());
                            network.setCreatedAt(LocalDateTime.now());
                            network.setUpdatedAt(LocalDateTime.now());
                        }
                    }
                    return network;
                }
        ).toList();
    }
}
