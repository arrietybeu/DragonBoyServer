package nro.server.realtime.dispatcher;

import lombok.Getter;
import nro.server.realtime.core.GameDispatcher;
import nro.server.realtime.core.IDispatcherBase;

@GameDispatcher
public class MonsterSystemDispatcher implements IDispatcherBase {

    @Getter
    private static final MonsterSystemDispatcher instance = new MonsterSystemDispatcher();

    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }
}
