package xyz.beriholic.beeyes.task;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.WarnMessage;
import xyz.beriholic.beeyes.entity.vo.response.MachineInfoVO;
import xyz.beriholic.beeyes.service.MachineService;
import xyz.beriholic.beeyes.service.MessageService;
import xyz.beriholic.beeyes.utils.InfluxDBUtils;

import java.util.List;

@Component
public class CPUMonitoringTask {
    @Resource
    InfluxDBUtils influxDBUtils;
    @Resource
    MessageService messageService;
    @Resource
    MachineService machineService;

    @Value("monitor.time")
    String time;

    @Scheduled(fixedRate = 300000) // 5min
    public void monitorCpuUsage() {
        List<Long> ids = influxDBUtils.queryCPUUsageLimited(Integer.parseInt(time));

        ids.forEach(id -> {
            MachineInfoVO machineInfoVO = machineService.machineInfo(id);
            messageService.sendWarmMessage(
                    WarnMessage.newCPUWarnMessage(id, machineInfoVO.getName())
            );
        });
    }
}
