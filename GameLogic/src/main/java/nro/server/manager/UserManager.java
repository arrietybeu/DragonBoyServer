package nro.server.manager;

import lombok.Getter;
import nro.model.template.entity.UserInfo;
import nro.server.LogServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserManager {

    @Getter
    private static final UserManager instance = new UserManager();
    private final Map<Integer, UserInfo> userMap = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void add(UserInfo user) {
        if (user == null) throw new NullPointerException("User is null");
        this.lock.writeLock().lock();
        try {
            this.userMap.put(user.getId(), user);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void remove(UserInfo user) {
        if (user == null) throw new NullPointerException("User is null");
        this.lock.writeLock().lock();
        try {
            this.userMap.remove(user.getId());

        } finally {
            lock.writeLock().unlock();
        }
    }

    public UserInfo get(int id) {
        this.lock.readLock().lock();
        try {
            return this.userMap.get(id);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean contains(int id) {
        this.lock.readLock().lock();
        try {
            return this.userMap.containsKey(id);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public Map<Integer, UserInfo> getAllUsers() {
        this.lock.readLock().lock();
        try {
            return new HashMap<>(this.userMap);
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public int size() {
        this.lock.readLock().lock();
        try {
            return this.userMap.size();
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
