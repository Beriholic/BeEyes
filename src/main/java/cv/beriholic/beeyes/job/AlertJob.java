package cv.beriholic.beeyes.job;

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

    @Scheduled(cron = "0 * * * * ?")
    public void scanAlerts() {
        log.info("[AlertJob] Starting alert scan...");
        try {
            alertService.checkAllAlerts();
        } catch (Exception e) {
            log.error("[AlertJob] Failed to scan alerts", e);
        }
        log.info("[AlertJob] Alert scan finished.");
    }
}
