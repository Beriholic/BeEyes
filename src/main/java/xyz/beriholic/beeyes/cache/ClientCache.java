package xyz.beriholic.beeyes.cache;

import org.springframework.stereotype.Component;
import xyz.beriholic.beeyes.entity.dto.Client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientCache {
    private final Map<Integer, Client> cache = new ConcurrentHashMap<>();


    public void putCache(int id, Client client) {
        cache.put(id, client);
    }

    public Client getCache(int id) {
        return cache.get(id);
    }

    public Collection<Client> getAllCache() {
        return cache.values();
    }
}
