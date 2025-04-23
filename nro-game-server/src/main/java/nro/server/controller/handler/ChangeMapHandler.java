package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;
import nro.server.service.core.map.AreaService;

@APacketHandler(-23)
public class ChangeMapHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            if (player.getPoints().isDead()) return;
            AreaService.getInstance().playerChangerMapByWayPoint(player);
        } catch (Exception ex) {
            LogServer.LogException("ChangerMapHandler: " + ex.getMessage(), ex);
        }
    }
}
