package xyz.beriholic.beeyes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import xyz.beriholic.beeyes.entity.dto.Machine;

@Mapper
public interface ClientMapper extends BaseMapper<Machine> {
}
