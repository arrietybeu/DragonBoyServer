package nro.controller.handler;

import nro.model.player.Player;
import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.LogServer;
import nro.service.AreaService;

@APacketHandler(-39)
public class FinishLoadMapHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            AreaService.getInstance().sendInfoAllPlayerInArea(player);
        } catch (Exception ex) {
            LogServer.LogException("FinishLoadMapHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
