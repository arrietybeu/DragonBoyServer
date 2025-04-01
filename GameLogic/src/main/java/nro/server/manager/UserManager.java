package nro.server.manager;

import lombok.Getter;
import nro.server.service.model.template.entity.UserInfo;
import nro.server.system.LogServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class UserManager {

    @Getter
    private static final UserManager instance = new UserManager();
    private final Map<Integer, UserInfo> userMap = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void add(UserInfo user) {
        if (user == null)
            throw new NullPointerException("User is null");
        this.lock.writeLock().lock();
        try {
            if (this.userMap.containsKey(user.getId())) {
                LogServer.LogWarning("User already exists: " + user.getId());
                SessionManager.getInstance().kickSession(user.getSession());
                return;
            }
            if (this.userMap.values().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
                LogServer.LogWarning("User name already exists: " + user.getUsername());
                SessionManager.getInstance().kickSession(user.getSession());
                return;
            }
            this.userMap.put(user.getId(), user);
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void remove(UserInfo user) {
        if (user == null)
            throw new NullPointerException("User is null");
        this.lock.writeLock().lock();
        try {
            if (!this.userMap.containsKey(user.getId())) {
                LogServer.LogWarning("User not found: " + user.getId());
                return;
            }
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

    public void checkUserName() {
        this.lock.readLock().lock();
        try {
            this.userMap.values().forEach(user -> {
                LogServer.LogWarning("User name is too short: " + user.getUsername());
            });
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean checkUserName(String userName) {
        this.lock.readLock().lock();
        try {
            return this.userMap.values().stream().anyMatch(user -> user.getUsername().equals(userName));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public boolean checkUserNameLogin(String userName) {
        this.lock.readLock().lock();
        try {
            return this.userMap.values().stream().anyMatch(user -> user.getUsername().equals(userName));
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public UserInfo checkUserLogin(String userName) {
        this.lock.readLock().lock();
        try {
            return this.userMap.values().stream().filter(user -> user.getUsername().equals(userName)).findFirst()
                    .orElse(null);
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
