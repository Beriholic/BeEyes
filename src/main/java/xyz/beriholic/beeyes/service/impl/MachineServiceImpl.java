package xyz.beriholic.beeyes.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.beriholic.beeyes.cache.MachineCache;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.dto.ClientSSH;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.vo.request.MachineNewVO;
import xyz.beriholic.beeyes.entity.vo.request.MachineUpdateVO;
import xyz.beriholic.beeyes.entity.vo.request.RenameClientVO;
import xyz.beriholic.beeyes.entity.vo.request.SSHInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.SSHInfoSaveVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.mapper.ClientSSHMapper;
import xyz.beriholic.beeyes.service.MachineService;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
public class MachineServiceImpl extends ServiceImpl<ClientMapper, Machine> implements MachineService {
    @Resource
    MachineCache machineCache;
    @Resource
    ClientDetailMapper clientDetailMapper;
    @Resource
    ClientSSHMapper clientSSHMapper;

    @Override
    @Transactional
    public void renameMachine(RenameClientVO vo) {
        this.update(Wrappers.<Machine>update().eq("id", vo.getId()).set("name", vo.getName()));

        Machine machine = machineCache.getIdCache(vo.getId());
        if (Objects.isNull(machine)) {
            machine = this.getById(vo.getId());
        }
        machine.setName(vo.getName());
        machineCache.putIdCache(vo.getId(), machine);
    }

    @Override
    @Transactional
    public String newMachine(MachineNewVO vo) {
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

    @Override
    @Transactional
    public MachineInfoVO machineInfo(long id) {
        Machine machine = machineCache.getIdCache(id);

        if (Objects.isNull(machine)) {
            machine = this.getById(id);
            machineCache.putIdCache(id, machine);
        }

        ClientDetail clientDetail = clientDetailMapper.selectById(id);

        MachineInfoVO vo = new MachineInfoVO();

        vo.copyFrom(machine);
        vo.copyFrom(clientDetail);

        return vo;
    }

    @Override
    public SSHInfoVO sshInfo(long id) {
        ClientSSH ssh = clientSSHMapper.selectById(id);
        SSHInfoVO vo = new SSHInfoVO();
        vo.setUsername(ssh.getUsername());
        vo.setPort(ssh.getPort());
        return vo;
    }

    @Override
    public void saveSSHInfo(SSHInfoSaveVO vo) {
        ClientSSH ssh = clientSSHMapper.selectById(vo.getId());
        if (Objects.nonNull(ssh)) {
            if (!vo.getPassword().isBlank()) {
                ssh.setPassword(vo.getPassword());
            }
            if (!vo.getUsername().isBlank()) {
                ssh.setUsername(vo.getUsername());
            }
            ssh.setPort(vo.getPort());

            clientSSHMapper.updateById(ssh);
            return;
        }

        ssh = new ClientSSH();
        ssh.setId(vo.getId());
        ssh.setPort(vo.getPort());
        ssh.setUsername(vo.getUsername());
        ssh.setPassword(vo.getPassword());
        clientSSHMapper.insert(ssh);
        return;
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
