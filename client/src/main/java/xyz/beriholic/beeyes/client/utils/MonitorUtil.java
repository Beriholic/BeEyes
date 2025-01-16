package xyz.beriholic.beeyes.client.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;
import xyz.beriholic.beeyes.client.entity.MachineInfo;
import xyz.beriholic.beeyes.client.entity.NetworkInterfaceInfo;
import xyz.beriholic.beeyes.client.entity.RuntimeInfo;

import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Component
public class MonitorUtil {

    private final SystemInfo systemInfo = new SystemInfo();
    private final Properties systemProperties = System.getProperties();

    public MachineInfo fetchMachineInfo() {
        OperatingSystem os = systemInfo.getOperatingSystem();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();


        double memoryCapacity = hardware.getMemory().getTotal() / 1024.0 / 1024 / 1024;

        double diskCapacity = hardware.getDiskStores().stream().mapToDouble(disk -> disk.getSize() / 1024.0 / 1024 / 1024).sum();

        NetworkIF networkIF = this.fetchNetworkInterface(hardware);

        NetworkInterfaceInfo networkInterfaceInfo = new NetworkInterfaceInfo();
        if (networkIF != null) {
            networkInterfaceInfo.setName(networkIF.getName());
            networkInterfaceInfo.setIpv4Addr(networkIF.getIPv4addr());
            networkInterfaceInfo.setIpv6Addr(networkIF.getIPv6addr());
        }

        return new MachineInfo()
                .setOsArch(systemProperties.getProperty("os.arch"))
                .setOsName(os.getFamily())
                .setOsVersion(os.getVersionInfo().getVersion())
                .setOsBitSize(os.getBitness())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCpuCoreCount(hardware.getProcessor().getLogicalProcessorCount())
                .setMemorySize(memoryCapacity)
                .setDiskSize(diskCapacity)
                .setNetworkInterfaceInfo(networkInterfaceInfo);
    }

    public RuntimeInfo fetchRuntimeInfo() {
        double staticTime = 0.5;

        try {
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            NetworkIF networkIF = this.fetchNetworkInterface(hardware);
            CentralProcessor processor = hardware.getProcessor();

            double uploadSpeed = 0;
            if (networkIF != null) {
                uploadSpeed = networkIF.getBytesSent();
            }
            double downloadSpeed = 0;
            if (networkIF != null) {
                downloadSpeed = networkIF.getBytesRecv();
            }

            double diskReadSpeed = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum();
            double diskWriteSpeed = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum();

            long[] ticks = processor.getSystemCpuLoadTicks();

            Thread.sleep((long) (staticTime * 1000));

            networkIF = this.fetchNetworkInterface(hardware);

            if (networkIF != null) {
                uploadSpeed = (networkIF.getBytesSent() - uploadSpeed) / staticTime;
            }
            if (networkIF != null) {
                downloadSpeed = (networkIF.getBytesRecv() - downloadSpeed) / staticTime;
            }
            diskReadSpeed = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum() - diskReadSpeed) / staticTime;
            diskWriteSpeed = (hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum() - diskWriteSpeed) / staticTime;
            double memory = (hardware.getMemory().getTotal() - hardware.getMemory().getAvailable()) / 1024.0 / 1024 / 1024;
            double disk = Arrays.stream(File.listRoots())
                    .mapToLong(file -> file.getTotalSpace() - file.getFreeSpace()).sum() / 1024.0 / 1024 / 1024;

            return new RuntimeInfo()
                    .setCpuUsage(this.calculateCpuUsage(processor, ticks))
                    .setMemoryUsage(memory)
                    .setDiskUsage(disk)
                    .setNetworkUploadSpeed(uploadSpeed / 1024)
                    .setNetworkDownloadSpeed(downloadSpeed / 1024)
                    .setDiskReadSpeed(diskReadSpeed / 1024 / 1024)
                    .setDiskWriteSpeed(diskWriteSpeed / 1024 / 1024)
                    .setTimestamp(new Date().getTime());
        } catch (Exception e) {
            log.error("获取运行时数据出错", e);
        }
        return null;
    }

    private NetworkIF fetchNetworkInterface(HardwareAbstractionLayer hardware) {
        try {

            for (NetworkIF networkIF : hardware.getNetworkIFs()) {
                String[] ipv4Addr = networkIF.getIPv4addr();
                String[] ipv6Addr = networkIF.getIPv6addr();

                NetworkInterface networkInterface = networkIF.queryNetworkInterface();

                if (!networkInterface.isLoopback() && !networkInterface.isVirtual()
                        && !networkInterface.isPointToPoint() && networkInterface.isUp()
                        && (networkInterface.getName().startsWith("eth") || networkInterface.getName().startsWith("en") || networkInterface.getName().startsWith("wl"))
                        && (ipv4Addr.length > 0 || ipv6Addr.length > 0)) {
                    return networkIF;
                }
            }
        } catch (IOException e) {
            log.error("获取网络接口出错", e);
        }
        return null;
    }


    private double calculateCpuUsage(CentralProcessor processor, long[] prevTicks) {
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long cUser = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = cUser + nice + cSys + idle + ioWait + irq + softIrq + steal;
        return (cSys + cUser) * 1.0 / totalCpu;
    }
}
