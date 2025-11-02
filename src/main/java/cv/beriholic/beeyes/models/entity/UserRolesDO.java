package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import org.babyfish.jimmer.sql.*;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;


/**
 * <p>
 * 用户角色表：存储用户角色分配信息
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "user_roles")
public interface UserRolesDO extends BaseDO {

    /**
     * 角色分配记录ID
     */
    @Id
    long id();

    /**
     * 用户ID
     */
    @Key
    @Column(name = "user_id")
    long userId();

    /**
     * 角色类型（应用层维护枚举映射）
     */
    @Key
    short role();

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
     * 角色过期时间（可为空表示永不过期）
     */
    @Column(name = "expires_at")
    @Nullable
    LocalDateTime expiresAt();

}
