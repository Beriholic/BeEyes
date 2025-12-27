package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.*;

import java.util.List;


/**
 * <p>
 * 用户表：存储系统用户基本信息和账户状态
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "users")
public interface UserDO extends BaseDO {
    /**
     * 用户唯一标识
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    /**
     * 父账户ID（用于账户关联）
     */
    @IdView
    @Nullable
    Long parentId();

    /**
     * 用户名（唯一）
     */
    @Key
    @NotNull
    String username();

    /**
     * 邮箱地址（唯一）
     */
    @Key
    @NotNull
    String email();

    /**
     * 密码哈希值
     */
    @Column(name = "password_hash")
    @NotNull
    String passwordHash();

    /**
     * 用户全名
     */
    @Column(name = "full_name")
    @Nullable
    String fullName();

    /**
     * 手机号
     */
    @Nullable
    String phone();

    @OneToOne(mappedBy = "user")
    @Nullable
    UserRoleDO role();

    @OneToMany(mappedBy = "user")
    List<PermissionsDO> permissions();

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @Nullable
    UserDO parent();

    @OneToMany(mappedBy = "parent")
    List<UserDO> subUsers();
}
