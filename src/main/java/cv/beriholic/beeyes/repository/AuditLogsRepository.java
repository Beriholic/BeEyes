package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AuditLogsDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * AuditLogsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class AuditLogsRepository extends AbstractJavaRepository<AuditLogsDO, Long> {

    public AuditLogsRepository(JSqlClient sql) {
        super(sql);
    }
}

