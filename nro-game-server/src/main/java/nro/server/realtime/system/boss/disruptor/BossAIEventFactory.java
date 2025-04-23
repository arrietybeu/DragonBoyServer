package nro.server.realtime.system.boss.disruptor;

import com.lmax.disruptor.EventFactory;

public class BossAIEventFactory implements EventFactory<BossAIEvent> {

    @Override
    public BossAIEvent newInstance() {
        return new BossAIEvent();
    }

}
