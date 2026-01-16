package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.NotificationAttemptDO;
import cv.beriholic.beeyes.models.entity.NotificationAttemptDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * NotificationAttemptRepository - 通知尝试记录仓库
 * </p>
 *
 * @author Beriholic
 */
@Repository
public class NotificationAttemptRepository extends BaseRepository<NotificationAttemptDO, NotificationAttemptDOTable, Long> {

    public NotificationAttemptRepository(JSqlClient sql) {
        super(sql, NotificationAttemptDOTable.$);
    }

    /**
     * 根据告警日志ID和渠道名称查找尝试记录
     */
    public List<NotificationAttemptDO> findByAlertLogIdAndChannel(Long alertLogId, String channel) {
        return createQuery()
                .where(table.alertLogId().eq(alertLogId))
                .where(table.channel().eq(channel))
                .orderBy(table.attemptCount().desc())
                .select(table)
                .execute();
    }

    /**
     * 查找待重试的通知
     */
    public List<NotificationAttemptDO> findPendingRetries() {
        return createQuery()
                .where(table.status().eq(0)) // PENDING
                .orderBy(table.nextRetryAt().asc())
                .select(table)
                .execute();
    }
}
