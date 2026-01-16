package cv.beriholic.beeyes.job;

import cv.beriholic.beeyes.service.AlertExecutionLock;
import cv.beriholic.beeyes.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class AlertJob {
    private final AlertService alertService;
    private final AlertExecutionLock alertExecutionLock;

    @Scheduled(cron = "0 * * * * ?")
    public void scanAlerts() {
        String executionId = alertExecutionLock.tryAcquire();
        if (executionId == null) {
            log.warn("[AlertJob] Skipping scheduled scan - another execution is in progress");
            return;
        }
        try {
            log.info("[AlertJob] Starting alert scan...");
            alertService.checkAllAlerts();
        } catch (Exception e) {
            log.error("[AlertJob] Failed to scan alerts", e);
        } finally {
            alertExecutionLock.release(executionId);
            log.info("[AlertJob] Alert scan finished.");
        }
    }
}
