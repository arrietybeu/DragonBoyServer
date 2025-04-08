package nro.server.realtime.system.boss;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.realtime.system.boss.disruptor.BossDisruptorEngine;
import nro.server.service.model.entity.ai.boss.Boss;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class BossAISystem implements ISystemBase {

    @Getter
    private static final BossAISystem instance = new BossAISystem();

    @Getter
    private final List<Boss> bosses = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void register(Object object) {
        if (object instanceof Boss boss) {
            lock.writeLock().lock();
            try {
                bosses.add(boss);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }


    @Override
    public void unregister(Object object) {
        if (object instanceof Boss boss) {
            lock.writeLock().lock();
            try {
                bosses.remove(boss);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void removeAll() {
        lock.writeLock().lock();
        try {
            bosses.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update() {
        lock.readLock().lock();
        try {
            for (Boss boss : bosses) {
                BossDisruptorEngine.getInstance().submit(boss);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return bosses.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Boss getBossById(int bossId) {
        lock.readLock().lock();
        try {
            return bosses.stream()
                    .filter(boss -> boss.getId() == bossId)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.readLock().unlock();
        }
    }
}
