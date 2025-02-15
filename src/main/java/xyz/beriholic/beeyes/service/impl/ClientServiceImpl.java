package xyz.beriholic.beeyes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.cache.ClientCache;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfo;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.repo.ClientDetailRepo;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.utils.InfluxDBUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {
    @Resource
    ClientCache clientCache;
    @Resource
    ClientDetailMapper clientDetailMapper;
    @Resource
    ClientDetailRepo clientDetailRepo;
    @Resource
    InfluxDBUtils influxDBUtils;

    @PostConstruct
    public void initClientCache() {
        this.list().forEach(client -> clientCache.putIdCache(client.getId(), client));
    }

    @Override
    public Client getClientById(int id) {
        return clientCache.getIdCache(id);
    }

    @Override
    public Client getClientByToken(String token) {
        if (clientCache.hasTokenCache(token)) {
            return clientCache.getTokenCache(token);
        }
        Client client = this.getOne(new QueryWrapper<Client>().eq("token", token));
        if (Objects.nonNull(client)) {
            clientCache.putTokenCache(token, client);
        }
        return client;
    }

    @Override
    public boolean verifyAndRegister(String token) {
        if (clientCache.hasTokenCache(token)) {
            return true;
        }

        Client client = this.getOne(new QueryWrapper<Client>().eq("token", token));

        if (Objects.isNull(client)) {
            log.info("Token无效 {}", token);
            return false;
        }

        clientCache.putTokenCache(token, client);
        return true;
    }

    @Override
    public void reportClientInfo(int clientId, MachineInfoVO vo) {
        ClientDetail clientDetail = ClientDetail.from(clientId, vo);
        if (Objects.nonNull(clientDetailMapper.selectById(clientId))) {
            clientDetailMapper.updateById(clientDetail);
        } else {
            clientDetailMapper.insert(clientDetail);
        }

        UpdateWrapper<Client> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", clientId).set("active", "yes");
        this.update(wrapper);
    }

    @Override
    public void reportRuntimeInfo(int clientId, RuntimeInfoVO vo) {
        RuntimeInfo runtimeInfo = RuntimeInfo.from(clientId, vo);
        clientCache.putRuntimeInfoCache(clientId, runtimeInfo);
        influxDBUtils.writeRuntimeInfo(runtimeInfo.toDB());
    }

    @Override
    public List<ClientMetricVO> getAllClientMetric() {
        return clientCache.getAllIdCache().stream().map(client -> {
            ClientMetricVO metric = ClientMetricVO.from(client);

            ClientDetail clientDetail = clientDetailRepo.getClientDetailById(client.getId());

            metric.addDataFromClientDetail(clientDetail);

            RuntimeInfo runtimeInfo = clientCache.getRuntimeInfoCache(client.getId());

            metric.setOnline(false);
            if (Objects.nonNull(runtimeInfo) && System.currentTimeMillis() - runtimeInfo.getTimestamp() < 30 * 1000) {
                metric.addDataFromRuntimeInfo(runtimeInfo);
                metric.setOnline(true);
            }

            return metric;
        }).toList();
    }
}
