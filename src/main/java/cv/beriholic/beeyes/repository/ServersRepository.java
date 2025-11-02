package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.ServersDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
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
public class ServersRepository extends AbstractJavaRepository<ServersDO, Long> {

    public ServersRepository(JSqlClient sql) {
        super(sql);
    }
}

