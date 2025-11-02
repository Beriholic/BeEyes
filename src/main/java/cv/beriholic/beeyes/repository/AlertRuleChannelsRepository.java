package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AlertRuleChannelsDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * AlertRuleChannelsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class AlertRuleChannelsRepository extends AbstractJavaRepository<AlertRuleChannelsDO, Long> {

    public AlertRuleChannelsRepository(JSqlClient sql) {
        super(sql);
    }
}

