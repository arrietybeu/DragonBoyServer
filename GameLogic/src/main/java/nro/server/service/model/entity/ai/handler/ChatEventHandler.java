//package nro.server.service.model.entity.ai.handler;
//
//import nro.server.service.core.social.ChatService;
//import nro.server.service.model.entity.ai.AIState;
//import nro.server.service.model.entity.ai.AIStateHandler;
//import nro.server.service.model.entity.ai.AbstractAI;
//import nro.server.service.model.entity.ai.boss.Boss;
//import nro.server.service.model.entity.player.Player;
//import nro.server.system.LogServer;
//
//public class ChatEventHandler implements AIStateHandler {
//
//    /// this method is used to chat with the players in the area.
//
//    private static final long CHAT_DURATION = 10_000;
//
//
//    @Override
//    public void handle(AbstractAI ai) {
//        try {
//            switch (ai) {
//                case Boss boss -> {
////                    if (boss.getTextChat().isEmpty()) {
////                        boss.setState(AIState.SEARCHING);
////                    } else {
////                        ChatService.getInstance().chatMap(ai, boss.getTextChat());
////                    }
//
//                    Player target = boss.getEntityTargetAsPlayer();
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
//
//                    if (boss.getNextState() == null) {
//                        ChatService.getInstance().chatMap(ai, "đù má chúng mầy tau cho chúng mầy 10s để chạy đi");
//                        boss.onEnterStateWithDelay(AIState.CHAT, CHAT_DURATION, AIState.SEARCHING);
//                        return;
//                    }
//
//                    boss.trySwitchToNextState();
//
//
//                }
//                default ->
//                        LogServer.LogException("ChatEventHandler.handle: " + ai.getClass().getSimpleName() + " is not a Boss");
//            }
//        } catch (Exception exception) {
//            LogServer.LogException("MoveEventHandler.entityChat: " + exception.getMessage(), exception);
//        }
//    }
//}
