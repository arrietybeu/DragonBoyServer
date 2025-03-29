package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-15)
public class MeBackHouseHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            if (!player.getPoints().isDead()) return;
            player.getPoints().returnTownFromDead();
        } catch (Exception ex) {
            LogServer.LogException("MeBackHouseHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
