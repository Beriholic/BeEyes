package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.SshConnectionsDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * SshConnectionsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class SshConnectionsRepository extends AbstractJavaRepository<SshConnectionsDO, Long> {

    public SshConnectionsRepository(JSqlClient sql) {
        super(sql);
    }
}

