//package nro.server.service.model.entity.ai.handler;
//
//import nro.consts.ConstTypeObject;
//import nro.server.service.model.entity.Entity;
//import nro.server.service.model.entity.ai.AIState;
//import nro.server.service.model.entity.ai.AIStateHandler;
//import nro.server.service.model.entity.ai.AbstractAI;
//import nro.server.service.model.entity.ai.boss.Boss;
//import nro.server.service.model.entity.player.Player;
//import nro.server.service.model.map.areas.Area;
//import nro.server.system.LogServer;
//
//public class SearchingEventHandler implements AIStateHandler {
//
//    @Override
//    public void handle(AbstractAI ai) {
//        try {
//            switch (ai) {
//                case Boss boss -> {
//                    Area area = boss.getArea(); // hoặc boss.getArea()
//                    if (area == null) return;
//
//                    // tìm người chơi gần nhất
//                    Player nearestPlayer = this.findNearestPlayer(boss, area);
//
//                    // nếu tìm thấy người chơi thì chuyển trạng thái của boss sang đuổi, còn nếu không tìm thấy là đứng im
//                    if (nearestPlayer != null) {
//                        boss.setEntityTarget(nearestPlayer);
//                        boss.setState(AIState.CHASING);
//                    } else {
//                        if (boss.isAutoDespawn()) {
//                            boss.dispose();
//                            return;
//                        }
//                        boss.setState(AIState.IDLE);
//                    }
//                }
//                default -> LogServer.LogException("Not supported AI type: " + ai.getClass().getName());
//            }
//        } catch (Exception e) {
//            LogServer.LogException("SearchingEventHandler.handle: " + e.getMessage(), e);
//        }
//    }
//
//    private Player findNearestPlayer(Entity boss, Area area) {
//        Player nearest = null;
//        int minDistance = Integer.MAX_VALUE;
//
//        for (Entity entity : area.getEntitysByType(ConstTypeObject.TYPE_PLAYER)) {
//            if (entity instanceof Player player) {
//                if (!player.getPoints().isDead() && !player.getPlayerStatus().isInVisible()) {
//                    int distance = Math.abs(player.getX() - boss.getX());
//                    if (distance < minDistance) {
//                        minDistance = distance;
//                        nearest = player;
//                    }
//                }
//            }
//        }
//        return nearest;
//    }
//
//}
