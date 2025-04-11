package nro.server.realtime.dispatcher;

import lombok.Getter;
import nro.server.realtime.core.GameDispatcher;
import nro.server.realtime.core.IDispatcherBase;
import nro.server.realtime.system.player.PlayerSystem;
import nro.server.realtime.core.ISystemBase;
import nro.server.realtime.system.player.TradeSystem;
import nro.server.system.LogServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GameDispatcher
public class PlayerSystemDispatcher implements IDispatcherBase {

    @Getter
    private static final PlayerSystemDispatcher instance = new PlayerSystemDispatcher();

    private final List<ISystemBase> systems = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public void start() {
        registerSystems();
        scheduler.scheduleAtFixedRate(this::tick, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        this.tick(systems);
    }

    private void registerSystems() {
        systems.add(PlayerSystem.getInstance());
        systems.add(TradeSystem.getInstance());
        // TODO add system new to here
        // systems.add(SkillCooldownSystem.getInstance());
    }

    @Override
    public void stop() {
        for (ISystemBase system : systems) {
            if (Objects.requireNonNull(system) instanceof PlayerSystem playerSystem) {
                playerSystem.getPlayers().clear();
            } else {
                LogServer.LogException("System '" + system.name() + "' not supported for stop.");
            }
        }
        systems.clear();
        scheduler.shutdown();
    }
}

