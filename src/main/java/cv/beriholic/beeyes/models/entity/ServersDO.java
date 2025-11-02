package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;


/**
 * <p>
 * 服务器表：存储被监控服务器的详细信息
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "servers")
public interface ServersDO extends BaseDO {

    /**
     * 服务器唯一标识
     */
    @Id
    long id();

    /**
     * 所属服务器组ID
     */
    @Column(name = "group_id")
    @Nullable
    Long groupId();

    /**
     * 主机名
     */
    @Key
    @NotNull
    String hostname();

    /**
     * IP地址
     */
    @Column(name = "ip_address")
    @NotNull
    String ipAddress();

    /**
     * SSH端口
     */
    @Nullable
    Integer port();

    /**
     * 服务器描述
     */
    @Nullable
    String description();

    /**
     * 操作系统类型
     */
    @Column(name = "os_type")
    @Nullable
    String osType();

    /**
     * 操作系统版本
     */
    @Column(name = "os_version")
    @Nullable
    String osVersion();

    /**
     * CPU核心数
     */
    @Column(name = "cpu_cores")
    @Nullable
    Integer cpuCores();

    /**
     * 内存大小(GB)
     */
    @Column(name = "memory_gb")
    @Nullable
    Integer memoryGb();

    /**
     * 磁盘大小(GB)
     */
    @Column(name = "disk_gb")
    @Nullable
    Integer diskGb();

    /**
     * 服务器状态（应用层维护枚举映射）
     */
    @Nullable
    Integer status();

    /**
     * 最后在线时间
     */
    @Column(name = "last_seen")
    @Nullable
    LocalDateTime lastSeen();

    /**
     * API密钥
     */
    @Column(name = "api_key")
    @Nullable
    String apiKey();

}
