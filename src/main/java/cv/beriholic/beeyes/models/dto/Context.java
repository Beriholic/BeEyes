package cv.beriholic.beeyes.models.dto;

import lombok.Data;

@Data
public class Context {
    private Long userId;
    private String ip;
    private String userAgent;
    private String requestId;
    private Long timestamp;

    public Context() {
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Context{" +
                "userId='" + userId + '\'' +
                ", ip='" + ip + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
