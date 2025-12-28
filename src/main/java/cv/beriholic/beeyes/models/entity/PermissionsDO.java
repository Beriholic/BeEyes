package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;


/**
 * <p>
 * 用户权限表：存储用户具体权限分配信息
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "permissions")
public interface PermissionsDO extends BaseDO {

    /**
     * 权限分配记录ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    /**
     * 权限类型（应用层维护枚举映射）
     */
    @Key
    short permission();

    /**
     * 授权者ID
     */
    @Column(name = "granted_by")
    @Nullable
    Long grantedBy();

    /**
     * 授权时间
     */
    @Column(name = "granted_at")
    @Nullable
    LocalDateTime grantedAt();

    /**
     * 权限过期时间（可为空表示永不过期）
     */
    @Column(name = "expires_at")
    @Nullable
    LocalDateTime expiresAt();

    @Key
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDissociate(DissociateAction.DELETE)
    UserDO user();
}
