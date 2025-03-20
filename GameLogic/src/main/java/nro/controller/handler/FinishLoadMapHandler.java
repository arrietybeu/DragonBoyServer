package nro.controller.handler;

import nro.service.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.LogServer;
import nro.service.core.map.AreaService;

@APacketHandler(-39)
public class FinishLoadMapHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            AreaService.getInstance().sendInfoAllPlayerInArea(player);
        } catch (Exception ex) {
            LogServer.LogException("FinishLoadMapHandler: " + ex.getMessage(), ex);
        }
    }
}
