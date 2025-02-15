package xyz.beriholic.beeyes.cache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.Machine;
import xyz.beriholic.beeyes.entity.dto.RuntimeInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static xyz.beriholic.beeyes.consts.CacheKey.CLIENT_TOKEN_CACHE;

@Component
public class MachineCache {
    private final Map<Long, Machine> machineIdCache = new ConcurrentHashMap<>();
    private final Map<Long, RuntimeInfo> runtimeInfoCache = new ConcurrentHashMap<>();

    @Resource
    StringRedisTemplate clientTokenCache;

    public void putIdCache(long id, Machine machine) {
        machineIdCache.put(id, machine);
    }

    public Machine getIdCache(long id) {
        return machineIdCache.get(id);
    }

    public Collection<Machine> getAllIdCache() {
        return machineIdCache.values();
    }

    public void putTokenCache(String token, Machine machine) {
        String json = JSON.toJSONString(machine);
        clientTokenCache.opsForValue().set(CLIENT_TOKEN_CACHE + token, json);
    }

    public Boolean hasTokenCache(String token) {
        return clientTokenCache.hasKey(CLIENT_TOKEN_CACHE + token);
    }

    public Machine getTokenCache(String token) {
        String json = clientTokenCache.opsForValue().get(CLIENT_TOKEN_CACHE + token);
        return JSONObject.parseObject(json, Machine.class);
    }

    public void putRuntimeInfoCache(long clientId, RuntimeInfo info) {
        runtimeInfoCache.put(clientId, info);
    }

    public RuntimeInfo getRuntimeInfoCache(long clientId) {
        return runtimeInfoCache.get(clientId);
    }

    public void deleteIdCache(Long id) {
        this.machineIdCache.remove(id);
    }

    public void deleteTokenCache(String token) {
        clientTokenCache.delete(CLIENT_TOKEN_CACHE + token);
    }
}
