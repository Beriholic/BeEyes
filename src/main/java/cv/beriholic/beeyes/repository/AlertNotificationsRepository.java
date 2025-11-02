package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AlertNotificationsDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * AlertNotificationsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class AlertNotificationsRepository extends AbstractJavaRepository<AlertNotificationsDO, Long> {

    public AlertNotificationsRepository(JSqlClient sql) {
        super(sql);
    }
}

