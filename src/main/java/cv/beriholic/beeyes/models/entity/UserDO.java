package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
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
    long id();

    /**
     * 父账户ID（用于账户关联）
     */
    @Column(name = "parent_id")
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
     * 账户是否激活
     */
    @Column(name = "is_active")
    @Nullable
    Boolean isActive();

    /**
     * 是否为主账户
     */
    @Column(name = "is_main_account")
    @Nullable
    Boolean isMainAccount();

    /**
     * 手机号
     */
    @Nullable
    String phone();

    @ManyToMany
    @JoinTable(
            name = "user_servers",
            joinColumnName = "user_id",
            inverseJoinColumnName = "server_id"
    )
    List<ServersDO> servers();
}
