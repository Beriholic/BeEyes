package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 服务器表：存储被监控服务器的基本信息
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-19
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
    @Column(name = "hostname")
    @Nullable
    String hostname();

    /**
     * 服务器描述
     */
    @Nullable
    String description();

    /**
     * 服务器状态（应用层维护枚举映射）
     */
    @Column(name = "status")
    @Nullable
    Integer status();

    /**
     * 最后在线时间
     */
    @Column(name = "last_seen")
    @Nullable
    LocalDateTime lastSeen();

    /**
     * 客户端API密钥
     */
    @Column(name = "api_key")
    @Key
    @Nullable
    String apiKey();

    /**
     * 服务器地区
     */
    @Nullable
    String region();


    @OneToOne
    @JoinColumn(name = "hardware_id")
    @Nullable
    ServerHardwareDO hardware();

    @OneToMany(mappedBy = "server")
    List<ServerDiskDO> disks();

    @OneToMany(mappedBy = "server")
    List<ServerNetworkInterfacesDO> networkInterfaces();

    @ManyToMany(mappedBy = "servers")
    List<UserDO> users();
}
