package cv.beriholic.beeyes.service.alert;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory service for tracking metric snapshots over time.
 * Used to enforce duration-based alerting (e.g., trigger only if metric
 * has been above threshold for N seconds).
 */
@Service
@Slf4j
public class MetricSnapshotService {

    private static final int MAX_SNAPSHOTS_PER_KEY = 3600; // 1 hour at 1 snapshot/sec
    private static final long MAX_AGE_SECONDS = 3600; // 1 hour

    private final Map<String, MetricSnapshot> snapshots = new ConcurrentHashMap<>();

    /**
     * Record a metric value for a given rule and server.
     *
     * @param ruleId     the alert rule ID
     * @param serverId    the server ID
     * @param metricValue the current metric value
     * @param timestamp   when the measurement was taken
     */
    public void recordMetric(Long ruleId, Long serverId, double metricValue, LocalDateTime timestamp) {
        String key = buildKey(ruleId, serverId);
        MetricSnapshot snapshot = snapshots.computeIfAbsent(key, k -> new MetricSnapshot(ruleId, serverId));

        snapshot.addValue(metricValue, timestamp);
        cleanupOldEntries(key);
    }

    /**
     * Check if the metric has been continuously triggered for the specified duration.
     *
     * @param ruleId           the alert rule ID
     * @param serverId         the server ID
     * @param durationSeconds  required duration in seconds
     * @param currentValue     current metric value
     * @param conditionMet     whether the trigger condition is currently met
     * @return true if duration requirement is satisfied (condition met for durationSeconds)
     */
    public boolean hasMetDuration(Long ruleId, Long serverId, long durationSeconds,
                                   double threshold, boolean conditionMet) {
        if (durationSeconds <= 0) {
            return conditionMet;
        }

        String key = buildKey(ruleId, serverId);
        MetricSnapshot snapshot = snapshots.get(key);

        if (snapshot == null) {
            return false;
        }

        if (!conditionMet) {
            // Condition no longer met, reset tracking
            snapshot.clear();
            return false;
        }

        // Check if we have enough history to satisfy duration
        long satisfiedDuration = snapshot.getContinuousDuration(threshold);

        if (satisfiedDuration >= durationSeconds) {
            log.debug("Duration satisfied: ruleId={}, serverId={}, required={}s, actual={}s",
                    ruleId, serverId, durationSeconds, satisfiedDuration);
            return true;
        }

        log.debug("Duration not yet satisfied: ruleId={}, serverId={}, required={}s, actual={}s",
                ruleId, serverId, durationSeconds, satisfiedDuration);
        return false;
    }

    /**
     * Clear all snapshots for a specific rule and server.
     */
    public void clear(Long ruleId, Long serverId) {
        String key = buildKey(ruleId, serverId);
        snapshots.remove(key);
    }

    /**
     * Clear all snapshots (for testing).
     */
    public void clearAll() {
        snapshots.clear();
    }

    private void cleanupOldEntries(String key) {
        MetricSnapshot snapshot = snapshots.get(key);
        if (snapshot != null) {
            snapshot.removeOlderThan(LocalDateTime.now().minusSeconds(MAX_AGE_SECONDS));
            if (snapshot.isEmpty()) {
                snapshots.remove(key);
            }
        }
    }

    private String buildKey(Long ruleId, Long serverId) {
        return ruleId + ":" + serverId;
    }

    /**
     * Represents a time series of metric values for a specific rule-server pair.
     */
    private static class MetricSnapshot {
        private final Long ruleId;
        private final Long serverId;
        private final ConcurrentHashMap<LocalDateTime, Double> values;

        MetricSnapshot(Long ruleId, Long serverId) {
            this.ruleId = ruleId;
            this.serverId = serverId;
            this.values = new ConcurrentHashMap<>();
        }

        void addValue(double value, LocalDateTime timestamp) {
            // Avoid duplicate timestamps
            values.put(timestamp, value);

            // Enforce max size limit
            if (values.size() > MAX_SNAPSHOTS_PER_KEY) {
                removeOldest();
            }
        }

        void removeOlderThan(LocalDateTime cutoff) {
            values.keySet().removeIf(ts -> ts.isBefore(cutoff));
        }

        void removeOldest() {
            values.entrySet().stream()
                    .min(Map.Entry.comparingByKey())
                    .ifPresent(entry -> values.remove(entry.getKey()));
        }

        void clear() {
            values.clear();
        }

        boolean isEmpty() {
            return values.isEmpty();
        }

        /**
         * Calculate how long the metric has been continuously meeting the condition.
         * Looks backward from the most recent value and counts consecutive matches.
         * For EQ condition, checks if value equals threshold.
         */
        long getContinuousDuration(double threshold) {
            if (values.isEmpty()) {
                return 0;
            }

            // Sort by timestamp descending (newest first)
            List<Map.Entry<LocalDateTime, Double>> sortedEntries = values.entrySet().stream()
                    .sorted((a, b) -> b.getKey().compareTo(a.getKey()))
                    .toList();

            long continuousSeconds = 0;
            LocalDateTime previousTs = null;

            for (Map.Entry<LocalDateTime, Double> entry : sortedEntries) {
                double value = entry.getValue();
                // Check if value equals threshold (with small epsilon for floating point)
                if (Math.abs(value - threshold) < 0.0001) {
                    if (previousTs == null) {
                        // First (newest) entry, counts as 1 second
                        continuousSeconds = 1;
                    } else {
                        // Add time difference in seconds
                        long secondsBetween = Duration.between(entry.getKey(), previousTs).getSeconds();
                        continuousSeconds += secondsBetween;
                    }
                    previousTs = entry.getKey();
                } else {
                    // Condition no longer met, stop counting
                    break;
                }
            }

            return continuousSeconds;
        }
    }
}
