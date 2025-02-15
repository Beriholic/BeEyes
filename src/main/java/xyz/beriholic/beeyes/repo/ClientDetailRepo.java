package xyz.beriholic.beeyes.repo;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;

@Repository
public class ClientDetailRepo {
    @Resource
    ClientDetailMapper clientDetailMapper;

    public ClientDetail getClientDetailById(long id) {
        return clientDetailMapper.selectById(id);
    }
}
