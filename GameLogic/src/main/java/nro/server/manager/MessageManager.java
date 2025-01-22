package nro.server.manager;

import nro.network.Message;

import java.util.concurrent.ConcurrentHashMap;

public class MessageManager {

    private static final ConcurrentHashMap<Integer, Message> cache = new ConcurrentHashMap<>();

    public Message get(Integer key) {
        return cache.get(key);
    }

    public void put(Integer key, Message value) {
        cache.put(key, value);
    }

    public boolean contains(Integer key) {
        return cache.containsKey(key);
    }

    public void remove(Integer key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}
