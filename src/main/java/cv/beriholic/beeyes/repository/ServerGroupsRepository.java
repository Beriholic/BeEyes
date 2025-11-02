package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.ServerGroupsDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * ServerGroupsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class ServerGroupsRepository extends AbstractJavaRepository<ServerGroupsDO, Long> {

    public ServerGroupsRepository(JSqlClient sql) {
        super(sql);
    }
}

