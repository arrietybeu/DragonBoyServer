package nro.server.realtime.system.player;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class SkillSystem implements ISystemBase {

    @Getter
    private static final SkillSystem instance = new SkillSystem();
    private final List<Player> players = new ArrayList<>();
    private final Queue<DelayedSkillTask> delayedTasks = new ConcurrentLinkedQueue<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void scheduleDelaySkill(long delayMillis, Runnable action) {
        long executeAt = System.currentTimeMillis() + delayMillis;
        delayedTasks.add(new DelayedSkillTask(executeAt, action));
    }

    @Override
    public void register(Object object) {
        if (object instanceof Player player) {
            lock.writeLock().lock();
            try {
                players.add(player);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void unregister(Object object) {
        if (object instanceof Player player) {
            lock.writeLock().lock();
            try {
                players.remove(player);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void removeAll() {
        lock.writeLock().lock();
        try {
            players.clear();
            delayedTasks.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();

        while (!delayedTasks.isEmpty()) {
            DelayedSkillTask task = delayedTasks.peek();
            if (task != null && task.executeAt <= now) {
                try {
                    task.action.run();
                } catch (Exception e) {
                    LogServer.LogException("SkillSystem delayed task error", e);
                }
                delayedTasks.poll();
            } else {
                break;
            }
        }
    }

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public String name() {
        return "SkillSystem";
    }

    private record DelayedSkillTask(long executeAt, Runnable action) {
    }
}
