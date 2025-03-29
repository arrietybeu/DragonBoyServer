package nro.server.controller.handler;

import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.server.service.core.system.ResourceService;

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
