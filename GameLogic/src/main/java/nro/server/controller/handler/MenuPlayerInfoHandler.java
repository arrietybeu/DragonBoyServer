package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.player.PlayerService;

@APacketHandler(-79)
public class MenuPlayerInfoHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            var playerId = message.reader().readInt();
            PlayerService.getInstance().sendMenuPlayerInfo(player, playerId);
        } catch (Exception exception) {
            LogServer.LogException("MenuPlayerInfoHandler: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}
