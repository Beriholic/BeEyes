package xyz.beriholic.beeyes.client.task;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import xyz.beriholic.beeyes.client.api.ClientApi;
import xyz.beriholic.beeyes.client.entity.RuntimeInfo;
import xyz.beriholic.beeyes.client.utils.MonitorUtil;

@Slf4j
public class MonitorJobBean extends QuartzJobBean {
    @Resource
    private MonitorUtil monitorUtil;

    @Resource
    private ClientApi api;

    @Override
    protected void executeInternal(@Nonnull JobExecutionContext context) {
        RuntimeInfo runtimeInfo = monitorUtil.fetchRuntimeInfo();
        api.reportRuntimeInfo(runtimeInfo);
    }
}
