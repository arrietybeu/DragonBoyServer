package nro.server.service.model.entity.ai.handler;

import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.system.LogServer;

public class IdleEventHandler implements AIStateHandler {
    @Override
    public void handle(AbstractAI ai) {
        try {
            Boss boss = (Boss) ai;
            if (boss.getArea() == null) {
                boss.setState(AIState.GO_TO_MAP);
                return;
            }
            if (boss.getEntityTarget() == null) {
                boss.setState(AIState.SEARCHING);
            }
        } catch (Exception e) {
            LogServer.LogException("IdleEventHandler.handle: " + e.getMessage(), e);
        }
    }
}
