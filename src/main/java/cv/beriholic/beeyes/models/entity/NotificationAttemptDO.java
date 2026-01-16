package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 通知尝试记录表：存储通知发送的历史和重试信息
 * </p>
 *
 * @author Beriholic
 */
@Entity
@Table(name = "notification_attempts")
public interface NotificationAttemptDO extends BaseDO {

    /**
     * 记录ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    /**
     * 关联告警日志ID
     */
    @Column(name = "alert_log_id")
    long alertLogId();

    /**
     * 通知渠道（如 DINGTALK, EMAIL, LOG）
     */
    @Column(name = "channel")
    String channel();

    /**
     * 发送状态（PENDING=0, SUCCESS=1, FAILED=2）
     */
    @Column(name = "status")
    int status();

    /**
     * 尝试次数
     */
    @Column(name = "attempt_count")
    int attemptCount();

    /**
     * 最后尝试时间
     */
    @Column(name = "last_attempt_at")
    @Nullable
    LocalDateTime lastAttemptAt();

    /**
     * 下次重试时间
     */
    @Column(name = "next_retry_at")
    @Nullable
    LocalDateTime nextRetryAt();

    /**
     * 错误消息
     */
    @Column(name = "error_message")
    @Nullable
    String errorMessage();
}
