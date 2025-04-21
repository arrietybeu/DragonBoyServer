package nro.server.realtime.system.player;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.core.economy.TradeService;
import nro.server.service.core.economy.TradeSession;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.item.ItemMap;
import nro.server.service.model.map.areas.Area;
import nro.server.system.LogServer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
public final class TradeSystem implements ISystemBase {

    @Getter
    private static final TradeSystem instance = new TradeSystem();

    private static final int UNSET_DELAY = Integer.MIN_VALUE;
    private static final int TIMEOUT = 30_000;
    private static final int MAX_ID = Integer.MAX_VALUE - 1;

    private final AtomicInteger idTrade = new AtomicInteger(UNSET_DELAY);

    private final Map<Integer, TradeSession> tradeSessions = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public int increaseIdTrade() {
        return idTrade.updateAndGet(id -> (id >= MAX_ID || id < 0) ? 1 : id + 1);
    }

    @Override
    public void register(Object object) {
        if (object instanceof TradeSession tradeSession) {
            lock.writeLock().lock();
            try {
                tradeSessions.put(tradeSession.getIdTrade(), tradeSession);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void unregister(Object object) {
        if (object instanceof TradeSession tradeSession) {
            lock.writeLock().lock();
            try {
                tradeSession.reset();
                tradeSessions.remove(tradeSession.getIdTrade());
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public void removeAll() {
        lock.writeLock().lock();
        try {
            for (TradeSession tradeSession : tradeSessions.values()) {
                tradeSession.reset();
            }
            tradeSessions.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void update() {
        long now = System.currentTimeMillis();
        List<Integer> expiredSessions = new ArrayList<>();

        lock.readLock().lock();
        try {
            for (TradeSession session : tradeSessions.values()) {
                long elapsedTime = now - session.getCreateTime();
                if (session.getCreateTime() > 0 && elapsedTime > TIMEOUT) {
                    expiredSessions.add(session.getIdTrade());
                }
            }
        } catch (Exception exception) {
            LogServer.LogException("TradeSystem update error: " + exception.getMessage(), exception);
        } finally {
            lock.readLock().unlock();
        }

        if (!expiredSessions.isEmpty()) {
            lock.writeLock().lock();
            try {
                for (Integer id : expiredSessions) {
                    TradeSession session = tradeSessions.remove(id);
                    if (session != null) {
                        TradeService.getInstance().cancelTradeBySession(session);
                    }
                }
            } catch (Exception ex) {
                LogServer.LogException("TradeSystem remove expired sessions error: " + ex.getMessage(), ex);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return tradeSessions.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String name() {
        return Class.class.getSimpleName();
    }

}
