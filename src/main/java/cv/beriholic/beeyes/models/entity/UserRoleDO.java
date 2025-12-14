package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

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
@Table(name = "user_role")
public interface UserRoleDO extends BaseDO {
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    @IdView
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

    @OneToOne
    @OnDissociate(DissociateAction.DELETE)
    @JoinColumn(name = "user_id")
    UserDO user();
}
