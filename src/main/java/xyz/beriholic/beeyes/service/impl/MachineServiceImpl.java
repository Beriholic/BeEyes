package xyz.beriholic.beeyes.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.cache.ClientCache;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.vo.request.NewMachineVO;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.service.MachineService;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Service
public class MachineServiceImpl extends ServiceImpl<ClientMapper, Client> implements MachineService {
    @Resource
    ClientCache clientCache;


    @Override
    public void renameMachine(RenameClientVO vo) {
        this.update(Wrappers.<Client>update().eq("id", vo.getId()).set("name", vo.getName()));

        Client client = clientCache.getIdCache(vo.getId());
        client.setName(vo.getName());

        clientCache.putIdCache(vo.getId(), client);
    }

    @Override
    public String newMachine(NewMachineVO vo) {
        String token;
        do {
            token = generateRandomToken();
        } while (clientCache.hasTokenCache(token));


        Client client = new Client()
                .setName(vo.getName())
                .setToken(token)
                .setLocation(vo.getLocation())
                .setNodeName(vo.getNodeName())
                .setRegisterTime(new Date());

        this.save(client);

        return token;
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
