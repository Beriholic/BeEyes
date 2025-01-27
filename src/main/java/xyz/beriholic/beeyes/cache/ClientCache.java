package xyz.beriholic.beeyes.cache;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.Client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientCache {
    private final String CLIENT_TOKEN_CACHE = "client_token_cache";
    private final Map<Integer, Client> clientIdCache = new ConcurrentHashMap<>();

    @Resource
    StringRedisTemplate clientTokenCache;

    public void putIdCache(int id, Client client) {
        clientIdCache.put(id, client);
    }

    public Client getIdCache(int id) {
        return clientIdCache.get(id);
    }

    public Collection<Client> getAllCache() {
        return clientIdCache.values();
    }

    public void putTokenCache(String token, boolean exist) {
        clientTokenCache.opsForValue().set(CLIENT_TOKEN_CACHE + token, "1");
    }

    public Boolean getTokenCache(String token) {
        return clientTokenCache.hasKey(CLIENT_TOKEN_CACHE + token);
    }
}
