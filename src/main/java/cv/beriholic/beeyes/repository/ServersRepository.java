package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.ServersDOTable;
import cv.beriholic.beeyes.models.entity.dto.MachineView;
import cv.beriholic.beeyes.models.entity.dto.QueryServerSpec;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

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
        createUpdate().set(table.status(), status.getKey())
                .where(table.id().eq(id)).execute();
    }

    public Long getIdByApiKey(String token) {
        return createQuery().where(table.apiKey().eq(token)).select(table.id()).fetchOneOrNull();
    }

    public ServerStatusDTO getStatus(Long id) {
        Integer status = createQuery().where(table.id().eq(id)).select(table.status()).fetchOneOrNull();
        if (Objects.isNull(status)) {
            return ServerStatusDTO.UnknowStatus();
        }
        ServerStatus serverStatus = ServerStatus.of(status);
        return ServerStatusDTO.of(serverStatus);
    }

    public List<Long> getAllIds() {
        return createQuery().select(table.id()).execute();
    }

    public List<Long> getServerIdListOrderByStatus(int pageIndex, int pageSize) {
        return createQuery()
                .orderBy(table.status().asc())
                .select(table.id())
                .fetchPage(pageIndex, pageSize)
                .getRows();
    }


    public Page<MachineView> queryMachineListOrderByStatus(QueryServerSpec queryServerSpec, int pageIndex, int pageSize) {
        return createQuery()
                .where(queryServerSpec)
                .orderBy(table.status().asc())
                .select(table.fetch(MachineView.class))
                .fetchPage(pageIndex, pageSize);
    }

    public boolean userHasServer(Long serverId) {
        return createQuery()
                .where(table.id().eq(serverId))
                .exists();

    }
}

