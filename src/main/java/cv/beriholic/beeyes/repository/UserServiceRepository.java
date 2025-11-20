package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.UserServicesDO;
import cv.beriholic.beeyes.models.entity.UserServicesDOTable;
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
}
