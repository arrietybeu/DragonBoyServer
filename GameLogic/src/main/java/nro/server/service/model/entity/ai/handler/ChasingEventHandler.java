//package nro.server.service.model.entity.ai.handler;
//
//import nro.server.service.model.entity.ai.AIState;
//import nro.server.service.model.entity.ai.AIStateHandler;
//import nro.server.service.model.entity.ai.AbstractAI;
//import nro.server.service.model.entity.ai.boss.Boss;
//import nro.server.service.model.entity.player.Player;
//import nro.server.system.LogServer;
//
//public class ChasingEventHandler implements AIStateHandler {
//
//    private static final int ATTACK_RANGE = 60; // khoarng cacsh de boss tan cong
//
//    @Override
//    public void handle(AbstractAI ai) {
//        try {
//            switch (ai) {
//                case Boss boss -> {
//                    Player target = boss.getEntityTargetAsPlayer();
//
//                    boss.setLastPlayerTarget(target);
//
//                    // target không còn hoặc không hợp lệ
//                    if (target == null) {
//                        if (boss.getLastPlayerTarget() != null) {
//                            boss.getLastPlayerTarget().changeTypePlayerKill(0);
//                            boss.setLastPlayerTarget(null);
//                        }
//                        if (boss.isAutoDespawn()) {
//                            boss.dispose();
//                            return;
//                        }
//                    }
//                    if (ai.isValidTarget(target)) {
//                        boss.setEntityTarget(null);
//                        boss.setState(AIState.SEARCHING);
//                        boss.changeTypePlayerKill(0);
//                        return;
//                    }
//                    boss.changeTypePlayerKill(3);
//                    target.changeTypePlayerKill(3);
//                    int distance = Math.abs(boss.getX() - target.getX());
//
//                    // neu đủ gần → chuyển sang đánh
//                    if (distance <= ATTACK_RANGE) {
//                        boss.setState(AIState.ATTACKING);
//                    } else {
//                        // di chuyển về phía target
//                        MoveEventHandler.goToPlayer(boss, target);
//                    }
//                }
//                default -> LogServer.LogException("Not supported AI type: " + ai.getClass().getSimpleName());
//            }
//        } catch (Exception e) {
//            LogServer.LogException("ChasingEventHandler.handle: " + e.getMessage(), e);
//        }
//    }
//
//}
