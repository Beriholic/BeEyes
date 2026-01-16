package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertLogDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * AlertLogRepository
 * </p>
 *
 * @author Beriholic
 * @date 2025-12-28
 */
@Repository
public class AlertLogRepository extends BaseRepository<AlertLogDO, AlertLogDOTable, Long> {

    public AlertLogRepository(JSqlClient sql) {
        super(sql, AlertLogDOTable.$);
    }

    public List<AlertLogDO> findUnresolvedLogsNested(Long serverId, Long ruleId, int status) {
        return createQuery()
                .where(table.serverId().eq(serverId))
                .where(table.ruleId().eq(ruleId))
                .where(table.status().eq(status))
                .select(table)
                .execute();
    }

    public long countByStatus(int status) {
        return createQuery()
                .where(table.status().eq(status))
                .select(table)
                .execute()
                .size();
    }

    public List<AlertLogDO> findAlterLogByPage(int pageIndex, int pageSize) {
        return createQuery()
                .where(table.serverId().isNotNull())
                .where(table.status().isNotNull())
                .select(table)
                .fetchPage(pageIndex, pageSize)
                .getRows();
    }
}
