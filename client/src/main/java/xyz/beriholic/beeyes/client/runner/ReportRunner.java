package xyz.beriholic.beeyes.client.runner;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.client.api.ClientApi;
import xyz.beriholic.beeyes.client.utils.MonitorUtil;

@Slf4j
@Component
public class ReportRunner implements ApplicationRunner {
    @Resource
    private ClientApi api;

    @Resource
    private MonitorUtil monitorUtil;

    @Override
    public void run(ApplicationArguments args) {
        log.info("正在向服务端上报机器信息");
        api.reportMachineInfo(monitorUtil.fetchMachineInfo());
    }
}
