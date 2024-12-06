package xyz.beriholic.beeyes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.beriholic.beeyes.entity.dto.Account;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
