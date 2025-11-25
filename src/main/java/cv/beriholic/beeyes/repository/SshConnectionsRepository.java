package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.SshConnectionsDO;
import cv.beriholic.beeyes.models.entity.SshConnectionsDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * <p>
 * SshConnectionsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class SshConnectionsRepository extends BaseRepository<SshConnectionsDO, SshConnectionsDOTable, Long> {

    public SshConnectionsRepository(JSqlClient sql) {
        super(sql, SshConnectionsDOTable.$);
    }

    public void updateLastConnectTime(long serverId, Long userId) {
        createUpdate()
                .set(table.lastConnectionAt(), LocalDateTime.now())
                .where(
                        Predicate.and(
                                table.userId().eq(userId),
                                table.serverId().eq(serverId)
                        )
                ).execute();
    }
}

