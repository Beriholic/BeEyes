package cv.beriholic.beeyes.converter;

import cn.hutool.core.util.IdUtil;
import cv.beriholic.beeyes.models.dto.system.CPUInfo;
import cv.beriholic.beeyes.models.dto.system.MachineInfo;
import cv.beriholic.beeyes.models.dto.system.MemoryInfo;
import cv.beriholic.beeyes.models.dto.system.SystemInfo;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.dto.SaveServerInput;
import cv.beriholic.beeyes.utils.DiffUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ServerMachineConverter {
    public static @NonNull SaveServerInput.TargetOf_hardware buildHardware(MachineInfo machineInfo, ServersDO serversDO) {
        SaveServerInput.TargetOf_hardware hardware = new SaveServerInput.TargetOf_hardware();
        SystemInfo systemInfo = machineInfo.getSystemInfo();
        CPUInfo cpuInfo = machineInfo.getCpuInfo();
        MemoryInfo memoryInfo = machineInfo.getMemoryInfo();

        hardware.setOsName(systemInfo.getOsName());
        hardware.setOsVersion(String.valueOf(systemInfo.getOsVersion()));
        hardware.setKernelVersion(String.valueOf(systemInfo.getKernelVersion()));
        hardware.setCpuArch(systemInfo.getCpuArch());
        hardware.setCpuCores(cpuInfo.getCoreCount());
        hardware.setCpuName(cpuInfo.getName());
        hardware.setTotalMemory(memoryInfo.getTotalMemory());
        hardware.setTotalSwap(memoryInfo.getTotalSwap());

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
        return machineInfo.getDiskInfo().stream().map(diskInfo -> {
            SaveServerInput.TargetOf_disks disk = new SaveServerInput.TargetOf_disks();

            disk.setDiskName(diskInfo.getName());
            disk.setFileSystem(diskInfo.getFileSystem());
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

                    String newFileSystem = DiffUtils.replaceOrNotNull(existingDisk.getFileSystem(), diskInfo.getFileSystem());
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
}
