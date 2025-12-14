package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.UserRoleDO;
import cv.beriholic.beeyes.models.entity.UserRoleDOTable;
import cv.beriholic.beeyes.models.entity.dto.UpdateUserRoleInput;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * UserRoleRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class UserRoleRepository extends BaseRepository<UserRoleDO, UserRoleDOTable, Long> {

    public UserRoleRepository(JSqlClient sql) {
        super(sql, UserRoleDOTable.$);
    }

    public boolean existUserRole(Long userId, short code) {
        return createQuery()
                .where(table.id().eq(userId))
                .where(table.role().eq(code))
                .exists();
    }

    public void updateRole(UpdateUserRoleInput input) {
        createUpdate()
                .where(table.userId().eq(input.getUserId()))
                .set(table.role(), input.getRole())
                .execute();

    }
}

