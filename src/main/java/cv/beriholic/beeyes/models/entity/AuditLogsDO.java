package cv.beriholic.beeyes.models.entity;

import cv.beriholic.beeyes.models.entity.common.BaseDO;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;


/**
 * <p>
 * 审计日志表：存储系统操作的审计记录和安全日志
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-01
 */
@Entity
@Table(name = "audit_logs")
public interface AuditLogsDO extends BaseDO {

    /**
     * 审计记录ID
     */
    @Id
    long id();

    /**
     * 操作用户ID
     */
    @Column(name = "user_id")
    @Nullable
    Long userId();

    /**
     * 操作类型（应用层维护枚举映射）
     */
    short action();

    /**
     * 资源类型
     */
    @Column(name = "resource_type")
    @NotNull
    String resourceType();

    /**
     * 资源ID
     */
    @Column(name = "resource_id")
    @Nullable
    Long resourceId();

    /**
     * 操作前的数据
     */
    @Column(name = "old_values")
    @Nullable
    String oldValues();

    /**
     * 操作后的数据
     */
    @Column(name = "new_values")
    @Nullable
    String newValues();

    /**
     * 客户端IP地址
     */
    @Column(name = "ip_address")
    @Nullable
    String ipAddress();

    /**
     * 客户端用户代理
     */
    @Column(name = "user_agent")
    @Nullable
    String userAgent();

}
