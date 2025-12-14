package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.*;

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
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    /**
     * 接口名称
     */
    @Column(name = "interface_name")
    @NotNull
    String interfaceName();

    /**
     * IPv4地址
     */
    @Column(name = "ipv4_address")
    @Nullable
    String[] ipv4Address();

    /**
     * IPv6地址（单个地址）
     */
    @Column(name = "ipv6_address")
    @Nullable
    String[] ipv6Address();

    @ManyToOne
    @JoinColumn(name = "server_id")
    @OnDissociate(DissociateAction.DELETE)
    ServersDO server();
}