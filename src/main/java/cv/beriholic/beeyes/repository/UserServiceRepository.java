package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.UserServicesDO;
import cv.beriholic.beeyes.models.entity.UserServicesDOTable;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserServiceRepository extends BaseRepository<UserServicesDO, UserServicesDOTable, Long> {
    public UserServiceRepository(JSqlClient sql) {
        super(sql, UserServicesDOTable.$);
    }

    public List<Long> getServerIdsByUserId(Long userId) {
        return createQuery().where(table.userId().eq(userId)).select(table.serverId()).execute();
    }

    public Page<Long> getServerIdsByUserId(PageDTO<Long> userIdPage) {
        return createQuery()
                .where(table.userId().eq(userIdPage.getData()))
                .select(table.serverId())
                .fetchPage(userIdPage.getPageIndex(), userIdPage.getPageSize());
    }

    public void deleteByServerId(Long serverId) {
        createDelete().where(table.serverId().eq(serverId)).execute();
    }
}
