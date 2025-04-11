package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.player.PlayerService;

@APacketHandler(-20)
public class PlayerPickItemMapHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            long time = System.currentTimeMillis();
            if (time - player.getPlayerState().getLastTimePickItem() < 1000) return;
            var idItemMap = message.reader().readShort();
            PlayerService.getInstance().pickItem(player, idItemMap);
            player.getPlayerState().setLastTimePickItem(time);
        } catch (Exception e) {
            LogServer.LogException("PlayerPickItemMapHandler: " + e.getMessage(), e);
        }
    }
}
