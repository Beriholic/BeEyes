package xyz.beriholic.beeyes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.dto.ClientDetail;
import xyz.beriholic.beeyes.entity.vo.request.ClientReportVO;
import xyz.beriholic.beeyes.entity.vo.request.RuntimeInfoVO;
import xyz.beriholic.beeyes.mapper.ClientDetailMapper;
import xyz.beriholic.beeyes.mapper.ClientMapper;
import xyz.beriholic.beeyes.service.ClientService;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ClientServiceImpl extends ServiceImpl<ClientMapper, Client> implements ClientService {
    private final Map<String, Client> tokenCache = new ConcurrentHashMap<>();
    private final Map<Integer, Client> idCache = new ConcurrentHashMap<>();

    @Resource
    ClientDetailMapper clientDetailMapper;

    private String token = this.generateRandomToken();
    private Map<Integer, RuntimeInfoVO> runtimeData = new ConcurrentHashMap<>();

    @PostConstruct
    public void initClientCache() {
        this.list().forEach(this::putCache);
    }

    @Override
    public Client getClientById(int id) {
        return this.idCache.get(id);
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
        if (this.getRegisterToken().equals(token)) {
            int id = this.generateRandomId();
            Client client = new Client(id, "未命名主机", token, new Date(), "cn", "未命名节点");

            if (this.save(client)) {
                this.token = this.generateRandomToken();
                this.putCache(client);
                log.info("Client注册成功: {}", client);
                return true;
            }
        }
        log.info("注册失败, Token: {}", token);
        return false;
    }

    @Override
    public void reportClientInfo(int clientId, ClientReportVO vo) {
        ClientDetail clientDetail = ClientDetail.from(clientId, vo);

        log.info("Client {} report info: {}", clientId, vo);

        if (Objects.nonNull(clientDetailMapper.selectById(clientId))) {
            clientDetailMapper.updateById(clientDetail);
        } else {
            clientDetailMapper.insert(clientDetail);
        }
    }

    @Override
    public void reportRuntimeInfo(int clientId, RuntimeInfoVO vo) {
        runtimeData.put(clientId, vo);
    }

    private void putCache(Client client) {
        this.idCache.put(client.getId(), client);
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
