package nro.server.service.model.entity.ai.handler;

import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

public class ChasingEventHandler implements AIStateHandler {

    private static final int ATTACK_RANGE = 60; // khoarng cacsh de boss tan cong

    @Override
    public void handle(AbstractAI ai) {
        try {
            Player target = ai.getEntityTargetAsPlayer();
            System.out.println("ChasingEventHandler.handle: " + ai.getName() + " chasing target: " + (target != null ? target.getName() : "null"));
            // target không còn hoặc không hợp lệ
            if (target == null || target.getPoints().isDead() || target.getArea() != ai.getArea()) {
                ai.setEntityTarget(null);
                ai.setState(AIState.SEARCHING);
                return;
            }

            int distance = Math.abs(ai.getX() - target.getX());

            // neu đủ gần → chuyển sang đánh
            if (distance <= ATTACK_RANGE) {
                ai.setState(AIState.ATTACKING);
            } else {
                // di chuyển về phía target
                MoveEventHandler.goToPlayer(ai, target);
            }
        } catch (Exception e) {
            LogServer.LogException("ChasingEventHandler.handle: " + e.getMessage(), e);
        }
    }


}
