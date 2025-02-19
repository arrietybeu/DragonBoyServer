package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.PlayerService;

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
