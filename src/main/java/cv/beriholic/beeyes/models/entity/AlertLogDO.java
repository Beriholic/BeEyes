package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;

/**
 * <p>
 * 告警日志表：存储告警触发和恢复的历史记录
 * </p>
 *
 * @author Beriholic
 * @date 2025-12-28
 */
@Entity
@Table(name = "alert_logs")
public interface AlertLogDO extends BaseDO {

    /**
     * 日志ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    /**
     * 关联规则ID
     */
    @Column(name = "rule_id")
    long ruleId();

    /**
     * 关联服务器ID
     */
    @Column(name = "server_id")
    long serverId();

    /**
     * 触发时的指标值
     */
    @Column(name = "metric_value")
    @Nullable
    Double metricValue();

    /**
     * 告警内容
     */
    @Column(name = "message")
    @Nullable
    String message();

    /**
     * 告警状态（触发中/已恢复）
     */
    @Column(name = "status")
    int status();

    /**
     * 开始时间
     */
    @Column(name = "started_at")
    @Nullable
    LocalDateTime startedAt();

    /**
     * 恢复时间
     */
    @Column(name = "resolved_at")
    @Nullable
    LocalDateTime resolvedAt();
}
