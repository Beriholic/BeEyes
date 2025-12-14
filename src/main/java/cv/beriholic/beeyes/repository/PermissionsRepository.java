package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.PermissionsDO;
import cv.beriholic.beeyes.models.entity.PermissionsDOTable;
import org.babyfish.jimmer.sql.JSqlClient;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * PermissionsRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class PermissionsRepository extends BaseRepository<PermissionsDO, PermissionsDOTable, Long> {

    public PermissionsRepository(JSqlClient sql) {
        super(sql, PermissionsDOTable.$);
    }

    public List<PermissionsDO> findByUserId(Long userId) {
        return createQuery()
                .where(table.userId().eq(userId))
                .select(table)
                .execute();
    }

    public boolean existUserPermissions(Long userId, List<Short> permissionCodes) {
        return createQuery()
                .where(table.userId().eq(userId))
                .where(table.permission().in(permissionCodes))
                .exists();
    }
}

