package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;

/**
 * <p>
 * 服务器网络接口表：存储服务器网络接口信息
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-19
 */
@Entity
@Table(name = "server_network_interfaces")
public interface ServerNetworkInterfacesDO extends BaseDO {

    /**
     * 网络接口ID
     */
    @Id
    long id();

    /**
     * 关联服务器ID
     */
    @Column(name = "server_id")
    long serverId();

    /**
     * 接口名称
     */
    @Column(name = "interface_name")
    String interfaceName();

    /**
     * IPv4地址
     */
    @Column(name = "ipv4_address")
    @Nullable
    String ipv4Address();

    /**
     * IPv6地址（单个地址）
     */
    @Column(name = "ipv6_address")
    @Nullable
    String ipv6Address();

    /**
     * 接口是否活跃
     */
    @Column(name = "is_active")
    @Nullable
    Boolean isActive();
}