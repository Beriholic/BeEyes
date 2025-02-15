package xyz.beriholic.beeyes.cache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.Client;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static xyz.beriholic.beeyes.consts.CacheKey.CLIENT_TOKEN_CACHE;

@Component
public class ClientCache {
    private final Map<Long, Client> clientIdCache = new ConcurrentHashMap<>();
    private final Map<Long, RuntimeInfo> runtimeInfoCache = new ConcurrentHashMap<>();

    @Resource
    StringRedisTemplate clientTokenCache;

    public void putIdCache(long id, Client client) {
        clientIdCache.put(id, client);
    }

    public Client getIdCache(long id) {
        return clientIdCache.get(id);
    }

    public Collection<Client> getAllIdCache() {
        return clientIdCache.values();
    }

    public void putTokenCache(String token, Client client) {
        String json = JSON.toJSONString(client);
        clientTokenCache.opsForValue().set(CLIENT_TOKEN_CACHE + token, json);
    }

    public Boolean hasTokenCache(String token) {
        return clientTokenCache.hasKey(CLIENT_TOKEN_CACHE + token);
    }

    public Client getTokenCache(String token) {
        String json = clientTokenCache.opsForValue().get(CLIENT_TOKEN_CACHE + token);
        return JSONObject.parseObject(json, Client.class);
    }

    public void putRuntimeInfoCache(long clientId, RuntimeInfo info) {
        runtimeInfoCache.put(clientId, info);
    }

    public RuntimeInfo getRuntimeInfoCache(long clientId) {
        return runtimeInfoCache.get(clientId);
    }
}
