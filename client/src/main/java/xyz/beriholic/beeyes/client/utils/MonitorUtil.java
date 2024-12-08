package xyz.beriholic.beeyes.client.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;
import xyz.beriholic.beeyes.client.entity.InterfaceInfo;
import xyz.beriholic.beeyes.client.entity.MachineInfo;

import java.net.NetworkInterface;
import java.net.SocketException;
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

    private List<InterfaceInfo> fetchInterfaceInfo(HardwareAbstractionLayer hardware) {
        List<InterfaceInfo> interfaceInfos = new CopyOnWriteArrayList<>();
        try {
            List<NetworkIF> networkIFs = systemInfo.getHardware().getNetworkIFs();
            for (NetworkIF networkIF : networkIFs) {
                NetworkInterface networkInterface = networkIF.queryNetworkInterface();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                InterfaceInfo interfaceInfo = new InterfaceInfo(
                        networkIF.getName(),
                        networkIF.getIPv4addr(),
                        networkIF.getIPv6addr()
                );

                interfaceInfos.add(interfaceInfo);
            }
        } catch (SocketException e) {
            log.error("获取网络接口失败,", e);
        }
        return interfaceInfos;
    }
}
