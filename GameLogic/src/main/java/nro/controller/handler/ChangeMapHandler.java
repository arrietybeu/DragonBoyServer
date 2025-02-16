package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.network.Message;
import nro.network.Session;
import nro.server.LogServer;
import nro.service.AreaService;

@APacketHandler(-23)
public class ChangeMapHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            AreaService.getInstance().playerChangerMap(player);
        } catch (Exception ex) {
            LogServer.LogException("ChangerMapHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
