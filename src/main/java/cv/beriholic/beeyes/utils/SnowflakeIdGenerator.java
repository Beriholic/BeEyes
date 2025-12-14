package cv.beriholic.beeyes.utils;

import cn.hutool.core.util.IdUtil;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;

public class SnowflakeIdGenerator implements UserIdGenerator<Long> {
    @Override
    public Long generate(Class entityType) {
        return IdUtil.getSnowflakeNextId();
    }
}
