package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.ServersDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * ServersRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class ServersRepository extends BaseRepository<ServersDO, ServersDOTable, Long> {

    public ServersRepository(JSqlClient sql) {
        super(sql, ServersDOTable.$);
    }


    public void updateStatus(Long id, ServerStatus status) {
        createUpdate().where(table.getId().eq(id)).set(table.status(), status.getKey());
    }

    public Long getIdByApiKey(String token) {
        return createQuery().where(table.apiKey().eq(token)).select(table.id()).fetchOneOrNull();
    }
}

