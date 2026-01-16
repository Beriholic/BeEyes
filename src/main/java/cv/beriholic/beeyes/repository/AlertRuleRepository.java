package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDOTable;
import cv.beriholic.beeyes.models.entity.dto.QueryAlertRuleRequest;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
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

    public Page<AlertRuleDO> findRulesPage(QueryAlertRuleRequest request) {
        var query = createQuery();
        if (request.getName() != null && !request.getName().isBlank()) {
            query.where(table.name().ilike(request.getName()));
        }
        if (request.getServerId() != null && !request.getServerId().isBlank()) {
            Long serverId = Long.parseLong(request.getServerId());
            // Show global rules (serverId=null) + rules for this specific server
            query.where(
                    Predicate.or(
                            table.serverId().isNull(),
                            table.serverId().eq(serverId)
                    )
            );
        }
        // If serverId is null, show all rules (no server filter)
        return query
                .orderBy(table.id().desc())
                .select(table)
                .fetchPage(request.getPageIndex() - 1, request.getPageSize());
    }
}
