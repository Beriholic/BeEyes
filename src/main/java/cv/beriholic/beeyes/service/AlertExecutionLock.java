package cv.beriholic.beeyes.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory lock service to prevent concurrent alert execution.
 * Protects against overlapping manual triggers and scheduled scans.
 */
@Service
@Slf4j
public class AlertExecutionLock {

    private static final String ALERT_LOCK_KEY = "alert-execution";
    private static final long LOCK_TIMEOUT_MS = 5 * 60 * 1000; // 5 minutes max

    private final Map<String, LockState> locks = new ConcurrentHashMap<>();

    /**
     * Try to acquire the alert execution lock.
     *
     * @return executionId if lock acquired, null if already locked
     */
    public String tryAcquire() {
        String executionId = UUID.randomUUID().toString();
        LockState newState = new LockState(executionId, System.currentTimeMillis(), Thread.currentThread().getName());

        LockState existing = locks.putIfAbsent(ALERT_LOCK_KEY, newState);
        if (existing != null) {
            // Check if existing lock is stale
            if (System.currentTimeMillis() - existing.startTimeMs() > LOCK_TIMEOUT_MS) {
                log.warn("Alert lock was stale (held by {} for {}ms), replacing",
                        existing.threadName(), System.currentTimeMillis() - existing.startTimeMs());
                locks.put(ALERT_LOCK_KEY, newState);
                return executionId;
            }
            log.info("Alert execution already in progress (started {}ms ago by {})",
                    System.currentTimeMillis() - existing.startTimeMs(), existing.threadName());
            return null;
        }
        log.debug("Alert lock acquired: id={}, thread={}", executionId, Thread.currentThread().getName());
        return executionId;
    }

    /**
     * Release the alert execution lock.
     *
     * @param executionId the execution ID returned from tryAcquire
     * @return true if lock was released, false if lock was not owned by this execution
     */
    public boolean release(String executionId) {
        if (executionId == null) {
            return false;
        }
        LockState current = locks.get(ALERT_LOCK_KEY);
        if (current == null) {
            return false;
        }
        if (!current.executionId().equals(executionId)) {
            log.warn("Attempted to release lock with wrong executionId: expected={}, actual={}",
                    current.executionId(), executionId);
            return false;
        }
        locks.remove(ALERT_LOCK_KEY);
        log.debug("Alert lock released: id={}, held for {}ms", executionId,
                System.currentTimeMillis() - current.startTimeMs());
        return true;
    }

    /**
     * Check if alert execution is currently locked.
     */
    public boolean isLocked() {
        LockState current = locks.get(ALERT_LOCK_KEY);
        if (current == null) {
            return false;
        }
        if (System.currentTimeMillis() - current.startTimeMs() > LOCK_TIMEOUT_MS) {
            log.warn("Alert lock was stale (held by {} for {}ms), treating as unlocked",
                    current.threadName(), System.currentTimeMillis() - current.startTimeMs());
            locks.remove(ALERT_LOCK_KEY);
            return false;
        }
        return true;
    }

    private record LockState(String executionId, long startTimeMs, String threadName) {}
}
