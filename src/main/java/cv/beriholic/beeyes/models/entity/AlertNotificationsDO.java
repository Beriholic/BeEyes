package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;

import java.time.LocalDateTime;


/**
 * <p>
 * 告警通知表：存储告警事件的通知发送记录和状态
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "alert_notifications")
public interface AlertNotificationsDO extends BaseDO {

    /**
     * 通知记录ID
     */
    @Id
    long id();

    /**
     * 关联告警事件ID
     */
    @Column(name = "incident_id")
    long incidentId();

    /**
     * 通知通道ID
     */
    @Column(name = "channel_id")
    long channelId();

    /**
     * 通知发送状态（应用层维护枚举映射）
     */
    @Nullable
    Integer status();

    /**
     * 发送时间
     */
    @Column(name = "sent_at")
    @Nullable
    LocalDateTime sentAt();

    /**
     * 错误信息（发送失败时）
     */
    @Column(name = "error_message")
    @Nullable
    String errorMessage();

    /**
     * 重试次数
     */
    @Column(name = "retry_count")
    @Nullable
    Integer retryCount();

}
