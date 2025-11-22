package cv.beriholic.beeyes.models.entity;

import org.babyfish.jimmer.sql.*;

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

    @Key
    @Column(name = "user_id")
    long userId();

    @Key
    @Column(name = "server_id")
    long serverId();
}
