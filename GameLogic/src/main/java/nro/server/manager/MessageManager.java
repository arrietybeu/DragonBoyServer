package nro.server.manager;

import nro.server.network.Message;

import java.util.concurrent.ConcurrentHashMap;

public final class MessageManager {

    private static final ConcurrentHashMap<Integer, Message> cache = new ConcurrentHashMap<>();

    public static Message get(Integer key) {
        return cache.get(key);
    }

    public static void put(Integer key, Message value) {
        cache.put(key, value);
    }

    public static boolean contains(Integer key) {
        return cache.containsKey(key);
    }

    public static void remove(Integer key) {
        cache.remove(key);
    }

    public static void clear() {
        cache.clear();
    }
}
