package nro.server.realtime.system.player;

import lombok.Getter;
import nro.server.realtime.core.ISystemBase;
import nro.server.service.core.economy.TradeSession;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
public class TradeSystem implements ISystemBase {

    @Getter
    private static final TradeSystem instance = new TradeSystem();

    private final Map<Integer, TradeSession> tradeSessions = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void register(Object object) {
    }

    @Override
    public void unregister(Object object) {
    }

    @Override
    public void removeAll() {
    }

    @Override
    public void update() {
    }

    @Override
    public int size() {
        return tradeSessions.size();
    }
}
