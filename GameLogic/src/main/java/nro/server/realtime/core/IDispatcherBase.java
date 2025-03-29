package nro.server.realtime.core;

import nro.server.system.LogServer;

import java.util.List;

public interface IDispatcherBase {

    void start();

    void stop();

    default void tick(List<ISystemBase> systems) {
        long start = System.currentTimeMillis();

        for (ISystemBase system : systems) {
            long systemStart = System.currentTimeMillis();

            try {
                system.update();
            } catch (Exception e) {
                LogServer.LogException("Error in system: " + system.name(), e);
            }

            long systemTime = System.currentTimeMillis() - systemStart;

            if (systemTime > 30) {
                LogServer.LogException("System '" + system.name() + "' delay: " + systemTime + "ms");
            }
        }

        long timeSpent = System.currentTimeMillis() - start;
        if (timeSpent > 100) {
            LogServer.LogException("Tick delay: " + timeSpent + "ms");
        }
    }

}
