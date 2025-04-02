package nro.server.realtime.dispatcher;

import lombok.Getter;
import nro.server.realtime.core.GameDispatcher;
import nro.server.realtime.core.IDispatcherBase;
import nro.server.realtime.core.ISystemBase;
import nro.server.realtime.system.boss.BossAISystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GameDispatcher
public class BossSystemDispatcher implements IDispatcherBase {

    @Getter
    private static final BossSystemDispatcher instance = new BossSystemDispatcher();

    private final List<ISystemBase> systems = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void start() {
        registerSystems();
        scheduler.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void registerSystems() {
        systems.add(BossAISystem.getInstance());
        systems.add(BossMovementSystem.getInstance());
        systems.add(BossAttackSystem.getInstance());
    }

    private void tick() {
        this.tick(systems);
    }

    @Override
    public void stop() {
        systems.clear();
        scheduler.shutdown();
    }

}
