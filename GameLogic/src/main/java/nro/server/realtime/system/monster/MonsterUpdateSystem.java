package nro.server.realtime.system.monster;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.model.entity.monster.Monster;
import nro.server.system.LogServer;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MonsterUpdateSystem implements ISystemBase {

    @Getter
    private static final MonsterUpdateSystem instance = new MonsterUpdateSystem();
    private final List<Monster> monsters = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void register(Object object) {
        if (object instanceof Monster monster) {
            lock.writeLock().lock();
            try {
                if (!monsters.contains(monster)) {
                    monsters.add(monster);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void unregister(Object object) {
        if (object instanceof Monster monster) {
            lock.writeLock().lock();
            try {
                monsters.remove(monster);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void removeAll() {
        this.monsters.clear();
    }

    @Override
    public void update() {
        lock.readLock().lock();
        try {
            for (Monster monster : monsters) {
                try {
                    if (monster.getPoint().isDead()) {
                        if (Util.canDoWithTime(monster.getInfo().getLastTimeDie(), 5000)) {
                            monster.setLive();
                        }
                    } else {
                        if (!monster.isMonsterAttack()) continue;
                        if (Util.canDoWithTime(monster.getInfo().getLastTimeAttack(), 1000)) {
                            monster.attackPlayer();
                        }
                    }
                } catch (Exception e) {
                    LogServer.LogException("MonsterUpdateSystem: " + e.getMessage(), e);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        return 0;
    }
}
