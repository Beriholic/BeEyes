package cv.beriholic.beeyes.models.dto;

import lombok.Data;

@Data
public class Context {
    private Long userId;
    private String ip;
    private String userAgent;
    private String traceId;
    private Long timestamp;

    public Context() {
        this.timestamp = System.currentTimeMillis();
    }
}
