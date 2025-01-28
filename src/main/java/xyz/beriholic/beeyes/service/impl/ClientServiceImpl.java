package xyz.beriholic.beeyes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.cache.ClientCache;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.vo.request.MachineInfoVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.entity.vo.response.ClientMetricVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.service.ClientService;
import xyz.beriholic.beeyes.utils.InfluxDBUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {
    private final Map<String, Client> tokenCache = new ConcurrentHashMap<>();
    private final Map<Integer, RuntimeInfoVO> runtimeInfoCache = new ConcurrentHashMap<>();
    @Resource
    ClientCache clientCache;
    @Resource
    ClientDetailMapper clientDetailMapper;
    @Resource
    InfluxDBUtils influxDBUtils;

    private String token = this.generateRandomToken();

    @PostConstruct
    public void initClientCache() {
        this.list().forEach(this::putCache);
    }

    @Override
    public Client getClientById(int id) {
        return clientCache.getIdCache(id);
    }

    @Override
    public Client getClientByToken(String token) {
        return this.tokenCache.get(token);
    }

    @Override
    public String getRegisterToken() {
        return this.token;
    }

    @Override
    public boolean verifyAndRegister(String token) {
        if (clientCache.getTokenCache(token)) {
            log.info("Token无效 {}", token);
            return true;
        }

        long count = this.count(new QueryWrapper<Client>().eq("token", token));
        if (count == 0) {
            log.info("Token无效 {}", token);
            return false;
        }

        clientCache.putTokenCache(token, true);
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
    }

//
//    @Override
//    public void reportRuntimeInfo(int clientId, RuntimeInfoVO vo) {
//        runtimeInfoCache.put(clientId, vo);
//        influxDBUtils.writeRuntimeInfo(clientId, vo);
//    }

    @Override
    public List<ClientMetricVO> getAllClientMetric() {
        return clientCache.getAllCache().stream().map(client -> {
            ClientMetricVO metric = ClientMetricVO.from(client);

            ClientDetail clientDetail = clientDetailMapper.selectById(client.getId());

            metric.addDataFromClientDetail(clientDetail);

            RuntimeInfoVO runtime = runtimeInfoCache.get(client.getId());

            metric.setOnline(false);
//            if (Objects.nonNull(runtime) && System.currentTimeMillis() - runtime.getTimestamp() < 60 * 1000) {
//                metric.addDataFromRuntimeInfo(runtime);
//                metric.setOnline(true);
//            }

            return metric;
        }).toList();
    }

    private void putCache(Client client) {
        clientCache.putIdCache(client.getId(), client);
        this.tokenCache.put(client.getToken(), client);
    }

    private int generateRandomId() {
        return new Random().nextInt(90000000) + 10000000;
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
