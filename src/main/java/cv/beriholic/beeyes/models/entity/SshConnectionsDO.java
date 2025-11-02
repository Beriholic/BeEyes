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
 * SSH连接表：存储服务器SSH连接配置信息和认证方式
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "ssh_connections")
public interface SshConnectionsDO extends BaseDO {

    /**
     * SSH连接配置ID
     */
    @Id
    long id();

    /**
     * 关联服务器ID
     */
    @Column(name = "server_id")
    long serverId();

    /**
     * SSH用户名
     */
    @NotNull
    String username();

    /**
     * 认证方式（应用层维护枚举映射）
     */
    @Column(name = "auth_type")
    short authType();

    /**
     * 加密存储的认证凭据（密码或密钥）
     */
    @Column(name = "encrypted_credential")
    @NotNull
    String encryptedCredential();

    /**
     * SSH端口号
     */
    @Nullable
    Integer port();

    /**
     * 连接超时时间（秒）
     */
    @Column(name = "connection_timeout")
    @Nullable
    Integer connectionTimeout();

    /**
     * 私钥文件路径（密钥认证时使用）
     */
    @Column(name = "key_path")
    @Nullable
    String keyPath();

    /**
     * 连接配置是否启用
     */
    @Column(name = "is_active")
    @Nullable
    Boolean isActive();

    /**
     * 最后一次连接测试是否成功
     */
    @Column(name = "last_test_success")
    @Nullable
    Boolean lastTestSuccess();

    /**
     * 最后一次连接测试时间
     */
    @Column(name = "last_test_at")
    @Nullable
    LocalDateTime lastTestAt();

}
