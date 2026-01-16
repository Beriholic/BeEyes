package cv.beriholic.beeyes.service.dingtalk;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP client for DingTalk Robot API.
 */
@Slf4j
public class DingTalkClient {

    private static final int TIMEOUT_MS = 5000;

    private final DingTalkSigner signer;
    private final String webhookUrl;
    private final String secret;

    public DingTalkClient(DingTalkSigner signer, String webhookUrl, String secret) {
        this.signer = signer;
        this.webhookUrl = webhookUrl;
        this.secret = secret;
    }

    /**
     * Sends a DingTalk message to the configured webhook.
     *
     * @param message the message to send
     * @throws DingTalkException if the request fails
     */
    public void send(DingTalkMessage message) {
        String url = buildSignedUrl();
        try {
            HttpResponse response = HttpRequest.post(url)
                    .body(message.toJson())
                    .timeout(TIMEOUT_MS)
                    .header("Content-Type", "application/json")
                    .execute();

            if (!response.isOk()) {
                throw new DingTalkException("DingTalk API returned non-OK status: " + response.getStatus());
            }

            String body = response.body();
            if (body != null && body.contains("\"errcode\":0")) {
                log.debug("DingTalk message sent successfully");
            } else {
                throw new DingTalkException("DingTalk API error: " + body);
            }
        } catch (DingTalkException e) {
            throw e;
        } catch (Exception e) {
            throw new DingTalkException("Failed to send DingTalk message", e);
        }
    }

    private String buildSignedUrl() {
        if (secret == null || secret.isBlank()) {
            return webhookUrl;
        }
        // webhookUrl already contains ?access_token=xxx, so use & for additional params
        return webhookUrl + "&" + signer.computeSignedQuery(secret);
    }

    public static class DingTalkException extends RuntimeException {
        public DingTalkException(String message) {
            super(message);
        }

        public DingTalkException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
