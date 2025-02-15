package xyz.beriholic.beeyes.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.beriholic.beeyes.cache.ClientCache;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.MachineUpdateVO;
import xyz.beriholic.beeyes.entity.vo.request.NewMachineVO;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.service.MachineService;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Service
public class MachineServiceImpl extends ServiceImpl<ClientMapper, Client> implements MachineService {
    @Resource
    ClientCache clientCache;
    @Resource
    ClientDetailMapper clientDetailMapper;
    @Autowired
    private ClientService clientService;


    @Override
    @Transactional
    public void renameMachine(RenameClientVO vo) {
        this.update(Wrappers.<Client>update().eq("id", vo.getId()).set("name", vo.getName()));

        Client client = clientCache.getIdCache(vo.getId());
        client.setName(vo.getName());

        clientCache.putIdCache(vo.getId(), client);
    }

    @Override
    @Transactional
    public String newMachine(NewMachineVO vo) {
        String token;
        do {
            token = generateRandomToken();
        } while (clientCache.hasTokenCache(token));


        long id = IdUtil.getSnowflakeNextId();
        Client client = new Client()
                .setId(id)
                .setName(vo.getName())
                .setToken(token)
                .setLocation(vo.getLocation())
                .setNodeName(vo.getNodeName())
                .setRegisterTime(new Date());

        this.save(client);
        clientCache.putIdCache(id, client);

        return token;
    }

    @Override
    @Transactional
    public void deleteMachine(Long id) {
        Client client = clientCache.getIdCache(id);

        clientCache.deleteIdCache(id);
        clientCache.deleteTokenCache(client.getToken());
        this.removeById(id);
        clientDetailMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateMachine(MachineUpdateVO vo) {
        Client client = clientCache.getIdCache(vo.getId());

        client.setName(vo.getName())
                .setNodeName(vo.getNodeName())
                .setLocation(vo.getLocation());

        clientCache.putIdCache(vo.getId(), client);
        clientCache.putTokenCache(client.getToken(), client);
        this.updateById(client);
    }


    private String generateRandomToken() {
        String token = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(24);

        for (int i = 0; i < 24; i++) {
            builder.append(token.charAt(random.nextInt(token.length())));
        }

        log.info("Current Token: {}", builder);
        return builder.toString();
    }
}
