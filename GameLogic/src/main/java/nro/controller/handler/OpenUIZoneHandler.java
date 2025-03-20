package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.map.MapService;

@APacketHandler(29)
public class OpenUIZoneHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        MapService.getInstance().sendListUIArea(player);
    }
}
