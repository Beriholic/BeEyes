package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.ServerNetworkInterfacesDO;
import cv.beriholic.beeyes.models.entity.ServerNetworkInterfacesDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

@Repository
public class ServerNetworkInterfaceRepository extends BaseRepository<ServerNetworkInterfacesDO, ServerNetworkInterfacesDOTable, Long> {
    public ServerNetworkInterfaceRepository(JSqlClient sql) {
        super(sql, ServerNetworkInterfacesDOTable.$);
    }
}
