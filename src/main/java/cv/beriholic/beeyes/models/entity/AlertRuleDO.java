package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

/**
 * <p>
 * 告警规则表：存储用户自定义的告警规则
 * </p>
 *
 * @author Beriholic
 * @date 2025-12-28
 */
@Entity
@Table(name = "alert_rules")
public interface AlertRuleDO extends BaseDO {

    /**
     * 规则ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    /**
     * 规则名称
     */
    @Column(name = "name")
    String name();

    /**
     * 关联服务器ID（为空则对所有服务器生效）
     */
    @Column(name = "server_id")
    @Nullable
    Long serverId();

    /**
     * 指标类型（CPU/内存/磁盘/状态）
     */
    @Column(name = "metric_type")
    int metricType();

    /**
     * 判断条件（大于/小于/等于）
     */
    @Column(name = "condition")
    int condition();

    /**
     * 阈值
     */
    @Column(name = "threshold")
    double threshold();

    /**
     * 持续时间（秒）
     */
    @Column(name = "duration_seconds")
    @Nullable
    Integer durationSeconds();

    /**
     * 是否启用
     */
    @Column(name = "enabled")
    @Default("true")
    boolean enabled();

    /**
     * 沉默时间（秒）
     */
    @Column(name = "silence_seconds")
    @Default("0")
    Integer silenceSeconds();
}
