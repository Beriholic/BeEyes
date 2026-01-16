package cv.beriholic.beeyes.service.dingtalk;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * DingTalk Robot API signature computation using HMAC-SHA256.
 * Reference: https://developers.dingtalk.com/document/app/signature-based-robots
 */
public class DingTalkSigner {

    private static final String ALGORITHM = "HmacSHA256";

    /**
     * Computes the signed query string for DingTalk Robot API authentication.
     *
     * @param secret the robot signing secret
     * @return the signed URL query parameter string (e.g., "timestamp=xxx&sign=xxx")
     */
    public String computeSignedQuery(String secret) {
        long timestamp = Instant.now().toEpochMilli();
        String stringToSign = timestamp + "\n" + secret;
        String sign = computeHmacSha256Base64(secret, stringToSign);
        return "timestamp=" + timestamp + "&sign=" + sign;
    }

    private String computeHmacSha256Base64(String secret, String data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(Base64.getEncoder().encodeToString(hash), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC-SHA256", e);
        }
    }
}
