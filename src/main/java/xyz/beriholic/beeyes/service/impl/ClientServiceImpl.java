package xyz.beriholic.beeyes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.beriholic.beeyes.cache.MachineCache;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfo;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.utils.InfluxDBUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Machine> implements ClientService {
    @Resource
    MachineCache machineCache;
    @Resource
    ClientDetailMapper clientDetailMapper;
    @Resource
    InfluxDBUtils influxDBUtils;

    @PostConstruct
    public void initClientCache() {
        this.list().forEach(client -> machineCache.putIdCache(client.getId(), client));
    }

    @Override
    public Machine getClientById(long id) {
        return machineCache.getIdCache(id);
    }

    @Override
    @Transactional
    public Machine getClientByToken(String token) {
        if (machineCache.hasTokenCache(token)) {
            return machineCache.getTokenCache(token);
        }
        Machine machine = this.getOne(new QueryWrapper<Machine>().eq("token", token));
        if (Objects.nonNull(machine)) {
            machineCache.putTokenCache(token, machine);
        }
        return machine;
    }

    @Override
    @Transactional
    public boolean verifyAndRegister(String token) {
        if (machineCache.hasTokenCache(token)) {
            return true;
        }

        Machine machine = this.getOne(new QueryWrapper<Machine>().eq("token", token));

        if (Objects.isNull(machine)) {
            log.info("Token无效 {}", token);
            return false;
        }

        machineCache.putTokenCache(token, machine);
        return true;
    }

    @Override
    @Transactional
    public void reportClientInfo(long clientId, MachineInfoVO vo) {
        ClientDetail clientDetail = ClientDetail.from(clientId, vo);
        if (Objects.nonNull(clientDetailMapper.selectById(clientId))) {
            clientDetailMapper.updateById(clientDetail);
        } else {
            clientDetailMapper.insert(clientDetail);
        }

        UpdateWrapper<Machine> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", clientId).set("active", "yes");
        this.update(wrapper);

        machineCache.putIdCache(clientId, machineCache.getIdCache(clientId).setActive("yes"));
    }

    @Override
    @Transactional
    public void reportRuntimeInfo(long clientId, RuntimeInfoVO vo) {
        RuntimeInfo runtimeInfo = RuntimeInfo.from(clientId, vo);
        machineCache.putRuntimeInfoCache(clientId, runtimeInfo);
        influxDBUtils.writeRuntimeInfo(runtimeInfo.toDB());
    }

    @Override
    @Transactional
    public List<ClientMetricVO> getAllClientMetric() {
        return machineCache.getAllIdCache().stream().map(client -> {
            ClientMetricVO metric = ClientMetricVO.from(client);

            ClientDetail clientDetail = clientDetailMapper.selectById(client.getId());

            metric.addDataFromClientDetail(clientDetail);

            RuntimeInfo runtimeInfo = machineCache.getRuntimeInfoCache(client.getId());

            metric.setOnline(false);
            if (Objects.nonNull(runtimeInfo) && System.currentTimeMillis() - runtimeInfo.getTimestamp() < 30 * 1000) {
                metric.addDataFromRuntimeInfo(runtimeInfo);
                metric.setOnline(true);
            }

            return metric;
        }).toList();
    }
}
