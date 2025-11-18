package cv.beriholic.beeyes.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息发送结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendResult {
    /**
     * 是否发送成功
     */
    private Boolean success;

    /**
     * 消息ID
     */
    private String messageId;

    /**
     * 主题名称
     */
    private String topic;

    /**
     * 分区号
     */
    private Integer partition;

    /**
     * 偏移量
     */
    private Long offset;

    /**
     * 发送时间
     */
    private LocalDateTime sendTime;

    /**
     * 错误信息（如果发送失败）
     */
    private String errorMessage;

    /**
     * 异常堆栈（如果发送失败）
     */
    private String errorStackTrace;

    /**
     * 发送耗时（毫秒）
     */
    private Long durationMs;

    /**
     * 重试次数
     */
    private Integer retryCount;

    // 静态工厂方法
    public static SendResult success(String messageId, String topic, Integer partition, Long offset) {
        return SendResult.builder()
                .success(true)
                .messageId(messageId)
                .topic(topic)
                .partition(partition)
                .offset(offset)
                .sendTime(LocalDateTime.now())
                .retryCount(0)
                .build();
    }

    public static SendResult success(String messageId, String topic, Integer partition, Long offset, Long durationMs) {
        return SendResult.builder()
                .success(true)
                .messageId(messageId)
                .topic(topic)
                .partition(partition)
                .offset(offset)
                .sendTime(LocalDateTime.now())
                .durationMs(durationMs)
                .retryCount(0)
                .build();
    }

    public static SendResult failure(String messageId, String topic, String errorMessage, String errorStackTrace) {
        return SendResult.builder()
                .success(false)
                .messageId(messageId)
                .topic(topic)
                .sendTime(LocalDateTime.now())
                .errorMessage(errorMessage)
                .errorStackTrace(errorStackTrace)
                .retryCount(0)
                .build();
    }

    public static SendResult failure(String messageId, String topic, String errorMessage, String errorStackTrace, Long durationMs, Integer retryCount) {
        return SendResult.builder()
                .success(false)
                .messageId(messageId)
                .topic(topic)
                .sendTime(LocalDateTime.now())
                .errorMessage(errorMessage)
                .errorStackTrace(errorStackTrace)
                .durationMs(durationMs)
                .retryCount(retryCount)
                .build();
    }

    /**
     * 创建成功结果（无分区信息）
     */
    public static SendResult success(String messageId, String topic) {
        return success(messageId, topic, null, null);
    }

    /**
     * 创建失败结果（无错误堆栈）
     */
    public static SendResult failure(String messageId, String topic, String errorMessage) {
        return failure(messageId, topic, errorMessage, null);
    }
}