package cv.beriholic.beeyes.service.notification;

import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.NotificationAttemptDO;
import cv.beriholic.beeyes.models.entity.NotificationAttemptDODraft;
import cv.beriholic.beeyes.repository.NotificationAttemptRepository;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for handling notification retry logic with exponential backoff.
 * Tracks failed notifications and schedules retries.
 */
@Service
@Slf4j
public class NotificationRetryService {

    private static final int MAX_RETRY_ATTEMPTS = 5;
    // Exponential backoff: 1min, 5min, 15min, 1hr, 4hr
    private static final long[] BACKOFF_SECONDS = {60, 300, 900, 3600, 14400};

    private final NotificationAttemptRepository attemptRepository;
    private final List<NotificationChannel> channels;

    // In-memory tracking of pending retries (for quick lookup)
    private final ConcurrentHashMap<String, RetryContext> pendingRetries = new ConcurrentHashMap<>();

    public NotificationRetryService(NotificationAttemptRepository attemptRepository,
                                    List<NotificationChannel> channels) {
        this.attemptRepository = attemptRepository;
        this.channels = channels;
    }

    /**
     * Record a failed notification and schedule retry if needed.
     */
    public void recordFailure(long alertLogId, String channelName, String errorMessage, AlertLogDO alertLog, AlertRuleDO rule) {
        // Check existing attempt count
        List<NotificationAttemptDO> existing = attemptRepository.findByAlertLogIdAndChannel(alertLogId, channelName);
        int attemptCount = existing.isEmpty() ? 0 : existing.getFirst().attemptCount();

        if (attemptCount >= MAX_RETRY_ATTEMPTS) {
            log.warn("Max retry attempts ({}) reached for alertLogId={}, channel={}. Giving up.",
                    MAX_RETRY_ATTEMPTS, alertLogId, channelName);
            saveAttempt(alertLogId, channelName, NotificationStatus.FAILED, attemptCount + 1, errorMessage, null);
            return;
        }

        // Calculate next retry time
        long backoffSeconds = attemptCount < BACKOFF_SECONDS.length
                ? BACKOFF_SECONDS[attemptCount]
                : BACKOFF_SECONDS[BACKOFF_SECONDS.length - 1];
        LocalDateTime nextRetry = LocalDateTime.now().plusSeconds(backoffSeconds);

        // Save attempt record
        saveAttempt(alertLogId, channelName, NotificationStatus.PENDING, attemptCount + 1, errorMessage, nextRetry);

        // Track in memory for quick processing
        String key = buildKey(alertLogId, channelName);
        pendingRetries.put(key, new RetryContext(alertLogId, channelName, alertLog, rule, nextRetry));

        log.info("Scheduled retry #{} for alertLogId={}, channel={}, nextRetry={}",
                attemptCount + 1, alertLogId, channelName, nextRetry);
    }

    /**
     * Record a successful notification.
     */
    public void recordSuccess(long alertLogId, String channelName) {
        saveAttempt(alertLogId, channelName, NotificationStatus.SUCCESS, 1, null, null);

        String key = buildKey(alertLogId, channelName);
        pendingRetries.remove(key);

        log.debug("Notification success recorded for alertLogId={}, channel={}", alertLogId, channelName);
    }

    /**
     * Process pending retries (called by scheduler).
     */
    @Scheduled(fixedDelay = 60000) // Every minute
    public void processPendingRetries() {
        LocalDateTime now = LocalDateTime.now();

        pendingRetries.entrySet().removeIf(entry -> {
            RetryContext ctx = entry.getValue();
            if (ctx.nextRetry().isBefore(now)) {
                // Time to retry
                processRetry(ctx);
                return true; // Remove from pending, will be re-added if failed again
            }
            return false;
        });
    }

    private void processRetry(RetryContext ctx) {
        log.info("Processing retry for alertLogId={}, channel={}", ctx.alertLogId(), ctx.channelName());

        NotificationChannel channel = findChannel(ctx.channelName());
        if (channel == null) {
            log.error("Channel not found for retry: {}", ctx.channelName());
            return;
        }

        try {
            NotificationResult result = channel.notify(ctx.alertLog(), ctx.rule());
            if (result.isSuccess()) {
                recordSuccess(ctx.alertLogId(), ctx.channelName());
            } else {
                recordFailure(ctx.alertLogId(), ctx.channelName(), result.errorMessage(), ctx.alertLog(), ctx.rule());
            }
        } catch (Exception e) {
            log.error("Retry failed for alertLogId={}, channel={}", ctx.alertLogId(), ctx.channelName(), e);
            recordFailure(ctx.alertLogId(), ctx.channelName(), e.getMessage(), ctx.alertLog(), ctx.rule());
        }
    }

    private NotificationChannel findChannel(String channelName) {
        return channels.stream()
                .filter(c -> c.getClass().getSimpleName().contains(channelName))
                .findFirst()
                .orElse(null);
    }

    private void saveAttempt(long alertLogId, String channelName, NotificationStatus status,
                             int attemptCount, String errorMessage, LocalDateTime nextRetry) {
        NotificationAttemptDO attempt = NotificationAttemptDODraft.$.produce(draft -> {
            draft.setAlertLogId(alertLogId);
            draft.setChannel(channelName);
            draft.setStatus(status.getKey());
            draft.setAttemptCount(attemptCount);
            draft.setLastAttemptAt(LocalDateTime.now());
            draft.setNextRetryAt(nextRetry);
            draft.setErrorMessage(errorMessage);
        });

        attemptRepository.save(attempt, SaveMode.INSERT_ONLY);
    }

    private String buildKey(long alertLogId, String channelName) {
        return alertLogId + ":" + channelName;
    }

    private enum NotificationStatus {
        PENDING(0),
        SUCCESS(1),
        FAILED(2);

        private final int key;

        NotificationStatus(int key) {
            this.key = key;
        }

        int getKey() {
            return key;
        }
    }

    private record RetryContext(long alertLogId, String channelName, AlertLogDO alertLog,
                                AlertRuleDO rule, LocalDateTime nextRetry) {
    }
}
