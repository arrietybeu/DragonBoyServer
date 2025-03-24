package nro.controller.handler;

import nro.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.service.core.system.ResourceService;

@APacketHandler(11)
public class RequestMonsterHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            var id = message.reader().readShort();
            ResourceService.getInstance().sendMonsterData(player, id);
        } catch (Exception e) {
            LogServer.LogException("Error RequestMonsterHandler: " + e.getMessage(), e);
        }
    }
}
