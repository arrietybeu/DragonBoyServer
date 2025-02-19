package nro.controller.handler;

import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.LogServer;
import nro.service.ResourceService;

@APacketHandler(11)
public class RequestMobHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            var id = message.reader().readShort();
            ResourceService.getInstance().sendMonsterData(player, id);
        } catch (Exception e) {
            LogServer.LogException("Error RequestMobHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
