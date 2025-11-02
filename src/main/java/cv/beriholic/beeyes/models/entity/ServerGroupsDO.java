package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.*;


/**
 * <p>
 * 服务器组表：存储服务器分组信息，支持层级结构
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "server_groups")
public interface ServerGroupsDO extends BaseDO {

    /**
     * 服务器组ID
     */
    @Id
    long id();

    /**
     * 组名
     */
    @Key
    @NotNull
    String name();

    /**
     * 组描述
     */
    @Nullable
    String description();

    /**
     * 父组ID（支持层级分组）
     */
    @Key
    @Column(name = "parent_group_id")
    @Nullable
    Long parentGroupId();

}
