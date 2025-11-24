package cv.beriholic.beeyes.repository;

import cv.beriholic.beeyes.models.entity.UserDO;
import cv.beriholic.beeyes.models.entity.UserDOTable;
import cv.beriholic.beeyes.models.entity.dto.AuthUserSpec;
import cv.beriholic.beeyes.models.entity.dto.AuthUserView;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * UsersRepository 接口
 * </p>
 *
 * @author beriholic
 * @date 2025-11-01
 */
@Repository
public class UserRepository extends BaseRepository<UserDO, UserDOTable, Long> {
    public UserRepository(JSqlClient sql) {
        super(sql, UserDOTable.$);
    }

    public AuthUserView findByAuthSpec(AuthUserSpec spec) {
        return createQuery()
                .where(table.username().eqIf(spec.getUsername()))
                .where(table.email().eqIf(spec.getEmail()))
                .where(table.phone().eqIf(spec.getPhone()))
                .select(table.fetch(AuthUserView.class))
                .fetchOne();
    }

    public List<Long> getUserIdList(Long serverId) {
        return createQuery()
                .where(table.servers(serversDOTableEx ->
                        Predicate.and(
                                serversDOTableEx.id().eq(serverId)
                        )
                )).select(table.id())
                .execute();
    }
}

