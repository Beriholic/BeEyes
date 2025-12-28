package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * AlertRuleRepository
 * </p>
 *
 * @author Beriholic
 * @date 2025-12-28
 */
@Repository
public class AlertRuleRepository extends BaseRepository<AlertRuleDO, AlertRuleDOTable, Long> {

    public AlertRuleRepository(JSqlClient sql) {
        super(sql, AlertRuleDOTable.$);
    }

    public List<AlertRuleDO> findEnabledRules() {
        return createQuery()
                .where(table.enabled().eq(true))
                .select(table)
                .execute();
    }

    public List<AlertRuleDO> findRulesByServerId(Long serverId) {
        return createQuery()
                .where(table.serverId().eq(serverId))
                .select(table)
                .execute();
    }
}
