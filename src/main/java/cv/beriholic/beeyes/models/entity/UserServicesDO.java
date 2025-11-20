package cv.beriholic.beeyes.models.entity;

import org.babyfish.jimmer.sql.Column;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Table;

/**
 * <p>
 * 用户服务器表：存储服务器对于用户的作用域
 *
 * </p>
 *
 * @author Beriholic
 * @date 2025-11-20
 */
@Entity
@Table(name = "user_servers")
public interface UserServicesDO {
    @Id
    long id();

    @Column(name = "user_id")
    long userId();

    @Column(name = "server_id")
    long serverId();
}
