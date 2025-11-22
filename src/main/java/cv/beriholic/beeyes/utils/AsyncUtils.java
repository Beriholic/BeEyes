package cv.beriholic.beeyes.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncUtils {
    public static void withRetry(Runnable task, int maxRetries, long delayMs) {
        int attempts = 0;
        while (attempts <= maxRetries) {
            try {
                task.run();
                return;
            } catch (Exception e) {
                attempts++;
                if (attempts > maxRetries) {
                    log.error("Failed after {} attempts: {}", attempts, e.getMessage(), e);
                    throw new RuntimeException("Operation failed after " + attempts + " attempts", e);
                }
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Operation interrupted", ie);
                }
            }
        }
    }
}
