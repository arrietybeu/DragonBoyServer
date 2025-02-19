package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.LogServer;
import nro.service.AreaService;

@APacketHandler(-33)
public class ChangePrivateMapHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            AreaService.getInstance().playerChangerMapByWayPoint(player);
        } catch (Exception ex) {
            LogServer.LogException("ChangerMapHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
