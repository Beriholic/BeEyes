package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

/**
 * <p>
 * 服务器硬件信息表：存储服务器硬件配置信息
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-19
 */
@Entity
@Table(name = "server_hardware")
public interface ServerHardwareDO extends BaseDO {

    /**
     * 硬件信息ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();


    /**
     * 操作系统名称
     */
    @Column(name = "os_name")
    @Nullable
    String osName();

    /**
     * 操作系统版本
     */
    @Column(name = "os_version")
    @Nullable
    String osVersion();

    /**
     * 内核版本
     */
    @Column(name = "kernel_version")
    @Nullable
    String kernelVersion();

    /**
     * CPU架构
     */
    @Column(name = "cpu_arch")
    @Nullable
    String cpuArch();

    /**
     * CPU型号
     */
    @Column(name = "cpu_name")
    @Nullable
    String cpuName();

    /**
     * CPU核心数
     */
    @Column(name = "cpu_cores")
    @Nullable
    Integer cpuCores();

    /**
     * 总内存(字节)
     */
    @Column(name = "total_memory")
    @Nullable
    Long totalMemory();

    /**
     * 总交换分区(字节)
     */
    @Column(name = "total_swap")
    @Nullable
    Long totalSwap();
}