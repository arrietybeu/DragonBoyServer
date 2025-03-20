package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.player.PlayerService;

@APacketHandler(-20)
public class PlayerPickItemMapHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            long time = System.currentTimeMillis();
            if (time - player.getPlayerStatus().getLastTimePickItem() < 1000) return;
            var idItemMap = message.reader().readShort();
            PlayerService.getInstance().pickItem(player, idItemMap);
            player.getPlayerStatus().setLastTimePickItem(time);
        } catch (Exception e) {
            LogServer.LogException("PlayerPickItemMapHandler: " + e.getMessage(), e);
        }
    }
}
