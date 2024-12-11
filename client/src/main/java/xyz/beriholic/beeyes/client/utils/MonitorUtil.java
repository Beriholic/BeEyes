package xyz.beriholic.beeyes.client.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;
import xyz.beriholic.beeyes.client.entity.InterfaceInfo;
import xyz.beriholic.beeyes.client.entity.MachineInfo;
import xyz.beriholic.beeyes.client.entity.RuntimeInfo;

import java.io.File;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

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

        List<InterfaceInfo> interfaceInfos = fetchInterfaceInfo(hardware);

        return new MachineInfo()
                .setOsArch(systemProperties.getProperty("os.arch"))
                .setOsName(os.getFamily())
                .setOsVersion(os.getVersionInfo().getVersion())
                .setOsBitSize(os.getBitness())
                .setCpuName(hardware.getProcessor().getProcessorIdentifier().getName())
                .setCpuCoreCount(hardware.getProcessor().getLogicalProcessorCount())
                .setMemorySize(memoryCapacity)
                .setDiskSize(diskCapacity)
                .setInterfacesInfo(interfaceInfos);
    }

    public RuntimeInfo fetchRuntimeInfo() {
        double staticTime = 0.5;

        try {
            HardwareAbstractionLayer hardware = systemInfo.getHardware();
            NetworkIF networkIF = this.fetchInterface(hardware).getFirst();
            CentralProcessor processor = hardware.getProcessor();

            double uploadSpeed = networkIF.getBytesSent();
            double downloadSpeed = networkIF.getBytesRecv();

            double diskReadSpeed = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getReadBytes).sum();
            double diskWriteSpeed = hardware.getDiskStores().stream().mapToLong(HWDiskStore::getWriteBytes).sum();

            long[] ticks = processor.getSystemCpuLoadTicks();

            Thread.sleep((long) (staticTime * 1000));

            networkIF = this.fetchInterface(hardware).getFirst();

            uploadSpeed = (networkIF.getBytesSent() - uploadSpeed) / staticTime;
            downloadSpeed = (networkIF.getBytesRecv() - downloadSpeed) / staticTime;
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


    private List<InterfaceInfo> fetchInterfaceInfo(HardwareAbstractionLayer hardware) {
        List<InterfaceInfo> interfaceInfos = new CopyOnWriteArrayList<>();

        List<NetworkIF> networkIFS = this.fetchInterface(hardware);

        networkIFS.forEach(networkIF -> {
            InterfaceInfo interfaceInfo = new InterfaceInfo(
                    networkIF.getName(),
                    networkIF.getIPv4addr(),
                    networkIF.getIPv6addr()
            );
            interfaceInfos.add(interfaceInfo);
        });

        return interfaceInfos;
    }

    //TODO 待优化网络接口获取
    private List<NetworkIF> fetchInterface(HardwareAbstractionLayer hardware) {
        List<NetworkIF> interfaces = new CopyOnWriteArrayList<>();
        List<NetworkIF> networkIFs = hardware.getNetworkIFs();

        for (NetworkIF networkIF : networkIFs) {
            String[] ipv4Addresses = networkIF.getIPv4addr();

            for (String ipv4 : ipv4Addresses) {
                if (isPrivateIP(ipv4)) {
                    interfaces.add(networkIF);
                    break;
                }
            }
        }
        return interfaces;
    }

    private boolean isPublicIP(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            return !inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && !inetAddress.isSiteLocalAddress();
        } catch (Exception e) {
            log.error("解析 IP 地址失败: {}", ip, e);
            return false;
        }
    }

    private boolean isPrivateIP(String ip) {
        try {
//            return ip.startsWith("10.") || ip.startsWith("192.168.") || ip.startsWith("172.") && (ip.split("\\.")[1].equals("16") || ip.split("\\.")[1].equals("31"));
            return ip.startsWith("192.168.");
        } catch (Exception e) {
            log.error("解析 IP 地址失败: {}", ip, e);
            return false;
        }
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
