package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.PermissionsDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * PermissionsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class PermissionsRepository extends AbstractJavaRepository<PermissionsDO, Long> {

    public PermissionsRepository(JSqlClient sql) {
        super(sql);
    }
}

