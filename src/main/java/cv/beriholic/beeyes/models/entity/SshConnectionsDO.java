package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.*;

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
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    @Column(name = "server_id")
    long serverId();

    @Key
    @Column(name = "user_id")
    long userId();

    @NotNull
    String password();

    @NotNull
    String name();

    int port();

    @Column(name = "last_connection_at")
    @Nullable
    LocalDateTime lastConnectionAt();
}
