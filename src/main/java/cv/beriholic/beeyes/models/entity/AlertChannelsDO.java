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
 * 告警通道表：存储告警通知渠道配置（如邮件、短信、钉钉等）
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "alert_channels")
public interface AlertChannelsDO extends BaseDO {

    /**
     * 告警通道ID
     */
    @Id
    long id();

    /**
     * 通道名称
     */
    @NotNull
    String name();

    /**
     * 通道类型（应用层维护枚举映射）
     */
    short type();

    /**
     * 通道配置信息
     */
    @NotNull
    String config();

    /**
     * 通道是否启用
     */
    @Column(name = "is_enabled")
    @Nullable
    Boolean isEnabled();

    /**
     * 是否为默认通道
     */
    @Column(name = "is_default")
    @Nullable
    Boolean isDefault();

}
