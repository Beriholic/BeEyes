package xyz.beriholic.beeyes.client.utils;

import org.junit.jupiter.api.Test;
import xyz.beriholic.beeyes.client.entity.MachineInfo;
import xyz.beriholic.beeyes.client.entity.RuntimeInfo;


public class MonitorUtilTests {
    @Test
    public void testFetchMachineInfo() {
        final MonitorUtil monitorUtil = new MonitorUtil();
        MachineInfo machineInfo = monitorUtil.fetchMachineInfo();

        assert (machineInfo.getOsArch() != null);
        assert (machineInfo.getOsName() != null);
        assert (machineInfo.getOsVersion() != null);
        assert (machineInfo.getOsBitSize() == 32 || machineInfo.getOsBitSize() == 64);
        assert (machineInfo.getCpuCoreCount() != 0);
        assert (machineInfo.getMemorySize() > 0);
        assert (machineInfo.getDiskSize() > 0);

        System.out.println(machineInfo);
    }

    @Test
    public void testFetchRuntimeInfo() {
        final MonitorUtil monitorUtil = new MonitorUtil();
        RuntimeInfo runtimeInfo = monitorUtil.fetchRuntimeInfo();
        System.out.println(runtimeInfo);
    }
}
