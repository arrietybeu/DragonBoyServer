package nro.server.realtime.system.boss.disruptor;

import com.lmax.disruptor.EventHandler;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AIStateManager;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.system.LogServer;

public class BossAIEventHandler implements EventHandler<BossAIEvent> {
    @Override
    public void onEvent(BossAIEvent event, long sequence, boolean endOfBatch) {
        Boss boss = event.boss;
        if (boss == null || boss.getState() == AIState.DEAD) return;

        AIStateHandler handler = AIStateManager.getHandler(boss.getState());
        if (handler != null) {
            handler.handle(boss);
        } else {
            LogServer.LogWarning("No handler for state: " + boss.getState());
        }
    }
}
