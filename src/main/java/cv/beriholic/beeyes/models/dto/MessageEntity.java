package cv.beriholic.beeyes.models.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import cv.beriholic.beeyes.utils.MDCUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MessageEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 7046055239055821721L;

    /**
     * TraceId
     */
    private String traceId;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 业务ID，用于分区策略
     */
    private String businessId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 扩展字段，用于存储额外信息
     */
    private String extra;

    public MessageEntity(String businessId, String content, String extra) {
        this.messageId = UUID.randomUUID().toString();
        this.traceId = MDCUtil.getTraceId();
        this.content = content;
        this.businessId = businessId;
        this.createTime = LocalDateTime.now();
        this.extra = extra;
    }

    public MessageEntity(String businessId, String content) {
        this.messageId = UUID.randomUUID().toString();
        this.traceId = MDCUtil.getTraceId();
        this.content = content;
        this.businessId = businessId;
        this.createTime = LocalDateTime.now();
    }
}
