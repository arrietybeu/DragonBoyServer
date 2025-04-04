package nro.server.realtime.system.boss;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.entity.ai.handler.AttackingEventHandler;
import nro.server.service.model.entity.ai.handler.ChasingEventHandler;
import nro.server.service.model.entity.ai.handler.SearchingEventHandler;
import nro.server.system.LogServer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BossAISystem implements ISystemBase {

    @Getter
    private static final BossAISystem instance = new BossAISystem();
    private final Map<Integer, Boss> bosses = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void register(Object object) {
        if (object instanceof Boss boss) {
            lock.writeLock().lock();
            try {
                if (bosses.containsKey(boss.getId())) {
                    LogServer.LogException("Boss " + boss.getId() + " is already registered!");
                    return;
                }
                bosses.put(boss.getId(), boss);
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
                bosses.remove(boss.getId());
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void update() {
        lock.readLock().lock();
        try {
            for (Boss boss : bosses.values()) {
                try {
                    this.onUpdate(boss);
                } catch (Exception e) {
                    LogServer.LogException("BossAISystem Error, Boss: " + boss.getId(), e);
                }
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private void onUpdate(Boss boss) {
        // Implement the logic to update the boss state
        AIState aiState = boss.getState();
        switch (aiState) {
            case AIState.IDLE -> {
            }
            case AIState.SEARCHING -> SearchingEventHandler.handler(boss);
            case AIState.CHASING -> ChasingEventHandler.handler(boss);
            case AIState.ATTACKING -> AttackingEventHandler.handler(boss);
        }
    }

    @Override
    public int size() {
        return bosses.size();
    }
}
