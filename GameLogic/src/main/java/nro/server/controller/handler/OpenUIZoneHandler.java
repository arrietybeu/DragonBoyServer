package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.map.MapService;

@APacketHandler(29)
public class OpenUIZoneHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        MapService.getInstance().sendListUIArea(player);
    }
}
