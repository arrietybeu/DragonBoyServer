package nro.server.realtime.dispatcher;

import lombok.Getter;
import nro.server.realtime.core.GameDispatcher;
import nro.server.realtime.core.IDispatcherBase;
import nro.server.realtime.core.ISystemBase;
import nro.server.realtime.system.monster.MonsterUpdateSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GameDispatcher
public class MonsterSystemDispatcher implements IDispatcherBase {

    @Getter
    private static final MonsterSystemDispatcher instance = new MonsterSystemDispatcher();
    private final List<ISystemBase> systems = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void start() {
        registerSystems();
        scheduler.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
    }

    private void registerSystems() {
        systems.add(MonsterUpdateSystem.getInstance());
        // TODO add system new to here
    }

    private void tick() {
        tick(systems);
    }

}
