package nro.server.realtime.system.boss.disruptor;

import com.lmax.disruptor.EventHandler;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AIStateFactory;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.system.LogServer;

public class BossAIEventHandler implements EventHandler<BossAIEvent> {

    @Override
    public void onEvent(BossAIEvent event, long sequence, boolean endOfBatch) {
        try {
            Boss boss = event.boss;
            if (boss == null || boss.getState() == AIState.DEAD) return;

            AIStateHandler handler = AIStateFactory.getHandler(boss.getState());
            if (handler != null) {
                handler.handle(boss);
            } else {
                LogServer.LogWarning("No handler for state: " + boss.getState());
            }
        } catch (Exception e) {
            LogServer.LogException("Error in BossAIEventHandler: " + e.getMessage(), e);
        }
    }

}
