package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;

import java.time.LocalDateTime;


/**
 * <p>
 * 告警事件表：存储告警触发后的具体事件记录和处理状态
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "alert_incidents")
public interface AlertIncidentsDO extends BaseDO {

    /**
     * 告警事件ID
     */
    @Id
    long id();

    /**
     * 触发规则的ID
     */
    @Column(name = "rule_id")
    long ruleId();

    /**
     * 关联服务器ID
     */
    @Column(name = "server_id")
    @Nullable
    Long serverId();

    /**
     * 关联容器ID
     */
    @Column(name = "container_id")
    @Nullable
    Long containerId();

    /**
     * 告警标题
     */
    @NotNull
    String title();

    /**
     * 告警消息内容
     */
    @NotNull
    String message();

    /**
     * 告警严重级别（应用层维护枚举映射）
     */
    short severity();

    /**
     * 事件处理状态（应用层维护枚举映射）
     */
    @Nullable
    Integer status();

    /**
     * 触发时的详细数据
     */
    @Column(name = "trigger_data")
    @Nullable
    String triggerData();

    /**
     * 确认人ID
     */
    @Column(name = "acknowledged_by")
    @Nullable
    Long acknowledgedBy();

    /**
     * 确认时间
     */
    @Column(name = "acknowledged_at")
    @Nullable
    LocalDateTime acknowledgedAt();

    /**
     * 解决人ID
     */
    @Column(name = "resolved_by")
    @Nullable
    Long resolvedBy();

    /**
     * 解决时间
     */
    @Column(name = "resolved_at")
    @Nullable
    LocalDateTime resolvedAt();

    /**
     * 解决说明
     */
    @Column(name = "resolution_note")
    @Nullable
    String resolutionNote();

}
