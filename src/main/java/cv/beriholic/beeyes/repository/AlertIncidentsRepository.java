package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AlertIncidentsDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * AlertIncidentsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class AlertIncidentsRepository extends AbstractJavaRepository<AlertIncidentsDO, Long> {

    public AlertIncidentsRepository(JSqlClient sql) {
        super(sql);
    }
}

