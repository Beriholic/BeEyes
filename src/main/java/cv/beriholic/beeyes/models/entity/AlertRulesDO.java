package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;


/**
 * <p>
 * 告警规则表：存储系统告警规则配置和触发条件
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "alert_rules")
public interface AlertRulesDO extends BaseDO {

    /**
     * 告警规则ID
     */
    @Id
    long id();

    /**
     * 规则名称
     */
    @NotNull
    String name();

    /**
     * 规则描述
     */
    @Nullable
    String description();

    /**
     * 告警严重级别（应用层维护枚举映射）
     */
    short severity();

    /**
     * 条件类型（应用层维护枚举映射）
     */
    @Column(name = "condition_type")
    short conditionType();

    /**
     * 触发条件配置
     */
    @Column(name = "condition_config")
    @NotNull
    String conditionConfig();

    /**
     * 目标服务器列表
     */
    @Column(name = "target_servers")
    @Nullable
    String targetServers();

    /**
     * 目标服务器组列表
     */
    @Column(name = "target_groups")
    @Nullable
    String targetGroups();

    /**
     * 规则是否启用
     */
    @Column(name = "is_enabled")
    @Nullable
    Boolean isEnabled();

    /**
     * 冷却时间（分钟）
     */
    @Column(name = "cooldown_minutes")
    @Nullable
    Integer cooldownMinutes();

}
