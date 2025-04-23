package nro.server.realtime.dispatcher;

import lombok.Getter;
import nro.server.realtime.core.GameDispatcher;
import nro.server.realtime.core.IDispatcherBase;
import nro.server.realtime.core.ISystemBase;
import nro.server.realtime.system.map.ItemMapSystem;
import nro.server.system.LogServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GameDispatcher
public class MapSystemDispatcher implements IDispatcherBase {

    @Getter
    private static final MapSystemDispatcher instance = new MapSystemDispatcher();

    private final List<ISystemBase> systems = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void start() {
        registerSystems();
        scheduler.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        scheduler.shutdownNow();
        systems.clear();
        LogServer.DebugLogic("MapSystemDispatcher stopped.");
    }

    private void registerSystems() {
        systems.add(ItemMapSystem.getInstance());
    }

    private void tick() {
        long tickStart = System.currentTimeMillis();
        for (ISystemBase system : systems) {
            long systemStart = System.currentTimeMillis();
            try {
                system.update();
            } catch (Exception e) {
                LogServer.LogException("Error in MapSystem: " + system.name(), e);
            }
            long systemTime = System.currentTimeMillis() - systemStart;
            if (systemTime > 30) {
                LogServer.LogException("MapSystem '" + system.name() + "' delay: " + systemTime + "ms");
            }
        }
        long elapsed = System.currentTimeMillis() - tickStart;
        if (elapsed > 100) {
            LogServer.LogException("MapSystemDispatcher tick delay: " + elapsed + "ms");
        }
    }

}
