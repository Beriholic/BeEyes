package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;


/**
 * <p>
 * 告警规则通道关联表：存储告警规则与通知通道的多对多关系
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "alert_rule_channels")
public interface AlertRuleChannelsDO extends BaseDO {

    /**
     * 关联记录ID
     */
    @Id
    long id();

    /**
     * 告警规则ID
     */
    @Key
    @Column(name = "rule_id")
    long ruleId();

    /**
     * 告警通道ID
     */
    @Key
    @Column(name = "channel_id")
    long channelId();

    /**
     * 关联是否启用
     */
    @Column(name = "is_enabled")
    @Nullable
    Boolean isEnabled();

}
