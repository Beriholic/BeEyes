package xyz.beriholic.beeyes.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.beriholic.beeyes.cache.MachineCache;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.MachineUpdateVO;
import xyz.beriholic.beeyes.entity.vo.request.NewMachineVO;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.service.MachineService;

import java.security.SecureRandom;
import java.util.Date;

@Slf4j
@Service
public class MachineServiceImpl extends ServiceImpl<ClientMapper, Machine> implements MachineService {
    @Resource
    MachineCache machineCache;
    @Resource
    ClientDetailMapper clientDetailMapper;


    @Override
    @Transactional
    public void renameMachine(RenameClientVO vo) {
        this.update(Wrappers.<Machine>update().eq("id", vo.getId()).set("name", vo.getName()));

        Machine machine = machineCache.getIdCache(vo.getId());
        machine.setName(vo.getName());

        machineCache.putIdCache(vo.getId(), machine);
    }

    @Override
    @Transactional
    public String newMachine(NewMachineVO vo) {
        String token;
        do {
            token = generateRandomToken();
        } while (machineCache.hasTokenCache(token));


        long id = IdUtil.getSnowflakeNextId();
        Machine machine = new Machine()
                .setId(id)
                .setName(vo.getName())
                .setToken(token)
                .setLocation(vo.getLocation())
                .setNodeName(vo.getNodeName())
                .setRegisterTime(new Date());

        this.save(machine);
        machineCache.putIdCache(id, machine);

        return token;
    }

    @Override
    @Transactional
    public void deleteMachine(Long id) {
        Machine machine = machineCache.getIdCache(id);

        machineCache.deleteIdCache(id);
        machineCache.deleteTokenCache(machine.getToken());
        this.removeById(id);
        clientDetailMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateMachine(MachineUpdateVO vo) {
        Machine machine = machineCache.getIdCache(vo.getId());

        machine.setName(vo.getName())
                .setNodeName(vo.getNodeName())
                .setLocation(vo.getLocation());

        machineCache.putIdCache(vo.getId(), machine);
        machineCache.putTokenCache(machine.getToken(), machine);
        this.updateById(machine);
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
