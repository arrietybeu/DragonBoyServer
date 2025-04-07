package nro.server.realtime.system.boss.disruptor;

import com.lmax.disruptor.EventHandler;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.system.LogServer;

public class BossAIEventHandler implements EventHandler<BossAIEvent> {

    @Override
    public void onEvent(BossAIEvent event, long sequence, boolean endOfBatch) {
        try {
            Boss boss = event.boss;
            if (boss == null) return;
            if (boss.getController() != null) {
                boss.getController().update(boss);
            }
        } catch (Exception e) {
            LogServer.LogException("Error in BossAIEventHandler: " + e.getMessage(), e);
        }
    }

}
