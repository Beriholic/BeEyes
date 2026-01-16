package cv.beriholic.beeyes.service.notification;

import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Async wrapper for notification dispatch.
 * Prevents notification failures from blocking the alert check flow.
 */
@Service
@Slf4j
public class AsyncNotificationService {

    private final List<NotificationChannel> notificationChannels;
    private final NotificationRetryService retryService;

    public AsyncNotificationService(List<NotificationChannel> notificationChannels,
                                    NotificationRetryService retryService) {
        this.notificationChannels = notificationChannels;
        this.retryService = retryService;
    }

    @Async("alertNotificationExecutor")
    public void notifyAsync(AlertLogDO logEntry, AlertRuleDO rule) {
        // Preserve logging context from the calling thread
        String originalThreadName = Thread.currentThread().getName();
        try {
            MDC.put("alertRuleId", String.valueOf(rule.id()));
            MDC.put("alertLogId", String.valueOf(logEntry.id()));
            MDC.put("serverId", String.valueOf(logEntry.serverId()));
            Thread.currentThread().setName("alert-notify-" + rule.id());

            log.info("Starting async notification for rule: {}, channel count: {}",
                    rule.name(), notificationChannels.size());

            for (NotificationChannel channel : notificationChannels) {
                String channelName = channel.getClass().getSimpleName();
                try {
                    NotificationResult result = channel.notify(logEntry, rule);
                    if (result.isSuccess()) {
                        log.debug("Notification sent via {} for rule: {}", channelName, rule.name());
                    } else {
                        log.warn("Notification failed via {} for rule: {}, error: {}",
                                channelName, rule.name(), result.errorMessage());
                        if (result.retryable()) {
                            retryService.recordFailure(logEntry.id(), channelName,
                                    result.errorMessage(), logEntry, rule);
                        }
                    }
                } catch (Exception e) {
                    log.error("Exception notifying channel {} for rule: {}",
                            channelName, rule.name(), e);
                    retryService.recordFailure(logEntry.id(), channelName,
                            e.getMessage(), logEntry, rule);
                }
            }

            log.info("Completed async notification for rule: {}", rule.name());
        } finally {
            MDC.clear();
            Thread.currentThread().setName(originalThreadName);
        }
    }
}
