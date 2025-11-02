package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AlertRulesDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * AlertRulesRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class AlertRulesRepository extends AbstractJavaRepository<AlertRulesDO, Long> {

    public AlertRulesRepository(JSqlClient sql) {
        super(sql);
    }
}

