package nro.server.service.model.entity.ai.handler;

import nro.consts.ConstTypeObject;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.AIState;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.areas.Area;
import nro.server.system.LogServer;

public class SearchingEventHandler implements AIStateHandler {

    @Override
    public void handle(AbstractAI ai) {
        try {
            Area area = ai.getArea(); // hoặc boss.getArea()
            if (area == null) return;

            // tìm người chơi gần nhất
            Player nearestPlayer = findNearestPlayer(ai, area);

            // nếu tìm thấy người chơi thì chuyển trạng thái của boss sang đuổi, còn nếu không tìm thấy là đứng im
            if (nearestPlayer != null) {
                ai.setEntityTarget(nearestPlayer);
                ai.setState(AIState.CHASING);
            } else {
                ai.setState(AIState.IDLE);
            }
        } catch (Exception e) {
            LogServer.LogException("SearchingEventHandler.handle: " + e.getMessage(), e);
        }
    }

    private Player findNearestPlayer(Entity boss, Area area) {
        Player nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Player player : area.getPlayersByType(ConstTypeObject.TYPE_PLAYER)) {
            if (!player.getPoints().isDead() && !player.getPlayerStatus().isInVisible()) {
                int distance = Math.abs(player.getX() - boss.getX());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = player;
                }
            }
        }
        return nearest;
    }

}
