package cv.beriholic.beeyes.service.notification;

/**
 * Result of a notification send attempt.
 */
public record NotificationResult(
        boolean isSuccess,
        String errorMessage,
        boolean retryable
) {
    public static NotificationResult success() {
        return new NotificationResult(true, null, false);
    }

    public static NotificationResult failure(String errorMessage, boolean retryable) {
        return new NotificationResult(false, errorMessage, retryable);
    }
}
