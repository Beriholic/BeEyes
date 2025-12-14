package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import cv.beriholic.beeyes.utils.SnowflakeIdGenerator;
import jakarta.annotation.Nullable;
import org.babyfish.jimmer.sql.*;

/**
 * <p>
 * 服务器磁盘信息表：存储服务器磁盘分区信息
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-19
 */
@Entity
@Table(name = "server_disk")
public interface ServerDiskDO extends BaseDO {

    /**
     * 磁盘信息ID
     */
    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();


    /**
     * 磁盘名称
     */
    @Column(name = "disk_name")
    String diskName();

    /**
     * 文件系统类型
     */
    @Column(name = "file_system")
    @Nullable
    String fileSystem();

    /**
     * 磁盘类型（SSD/HDD等）
     */
    @Column(name = "disk_kind")
    @Nullable
    String diskKind();

    /**
     * 总容量(字节)
     */
    @Column(name = "total_bytes")
    long totalBytes();

    @ManyToOne
    @OnDissociate(DissociateAction.DELETE)
    @JoinColumn(name = "server_id")
    ServersDO server();
}