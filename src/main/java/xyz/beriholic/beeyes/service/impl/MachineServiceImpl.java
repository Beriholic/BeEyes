package xyz.beriholic.beeyes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.cache.ClientCache;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.service.MachineService;

@Service
public class MachineServiceImpl extends ServiceImpl<ClientMapper, Client> implements MachineService {
    @Resource
    ClientCache clientCache;


    @Override
    public void rename(RenameClientVO vo) {
        this.update(Wrappers.<Client>update().eq("id", vo.getId()).set("name", vo.getName()));

        Client client = clientCache.getIdCache(vo.getId());
        client.setName(vo.getName());

        clientCache.putIdCache(vo.getId(), client);
    }

}
