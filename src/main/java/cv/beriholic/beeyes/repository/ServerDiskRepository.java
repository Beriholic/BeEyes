package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.ServerDiskDO;
import cv.beriholic.beeyes.models.entity.ServerDiskDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

@Repository
public class ServerDiskRepository extends BaseRepository<ServerDiskDO, ServerDiskDOTable, Long> {
    public ServerDiskRepository(JSqlClient sql) {
        super(sql, ServerDiskDOTable.$);
    }
}
