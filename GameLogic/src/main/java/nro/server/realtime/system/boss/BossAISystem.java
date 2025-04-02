package nro.server.realtime.system.boss;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.system.LogServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BossAISystem implements ISystemBase {

    @Getter
    private static final BossAISystem instance = new BossAISystem();
    private final List<Boss> bosses = new ArrayList<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void register(Object object) {
        if (object instanceof Boss boss) {
            lock.writeLock().lock();
            try {
                if (!bosses.contains(boss)) {
                    bosses.add(boss);
                }
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
    public void update() {
        lock.readLock().lock();
        try {
            for (Boss boss : bosses) {
                try {
//                    boss.updateAI();
                } catch (Exception e) {
                    LogServer.LogException("BossAISystem Error, Boss: " + boss.getId(), e);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        return bosses.size();
    }
}
