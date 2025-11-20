package cv.beriholic.beeyes.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SUCCESS(0, "成功"),
    //20000 限流
    REQUEST_FREQUENCY(20000, "请求频繁，请稍后再试"),
    LOGIN_FREQUENCY(20002, "登录验证频繁，请稍后再试"),
    //40000 权限错误
    UNAUTHORIZED(401, "未授权，请登录后再试"),
    FORBIDDEN(403, "权限不足"),
    SYSTEM_ERROR(500, "系统错误，请联系管理员"),
    PARAM_INVALID(601, "参数不合法"),
    // 70000 Kafka相关错误
    KAFKA_SEND_FAILED(70001, "Kafka消息发送失败"),
    KAFKA_TIMEOUT(70002, "Kafka操作超时"),
    KAFKA_CONNECTION_ERROR(70003, "Kafka连接错误"),
    KAFKA_TOPIC_NOT_FOUND(70004, "Kafka主题不存在"),
    KAFKA_SERIALIZATION_ERROR(70005, "Kafka消息序列化失败"),
    KAFKA_BATCH_SEND_FAILED(70006, "Kafka批量发送失败"),
    RECORD_NOT_FOUND(80001, "记录未找到");

    private final Integer code;
    private final String msg;
}