package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.UserRolesDO;
import org.babyfish.jimmer.spring.repo.support.AbstractJavaRepository;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * UserRolesRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class UserRolesRepository extends AbstractJavaRepository<UserRolesDO, Long> {

    public UserRolesRepository(JSqlClient sql) {
        super(sql);
    }

}

