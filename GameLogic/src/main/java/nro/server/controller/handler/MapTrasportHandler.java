package nro.server.controller.handler;

import nro.consts.ConstPlayer;
import nro.consts.ConstsCmd;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

@APacketHandler(ConstsCmd.MAP_TRASPORT)
public class MapTrasportHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        final Player player = session.getPlayer();
        if (player == null) return;
        try {
            var selected = message.reader().readByte();
            switch (player.getPlayerStatus().getTypeTransport()) {
                case ConstPlayer.TYPE_TRANSPORT_CAPSULE ->
                        player.getPlayerTransport().playerTransport(player, selected);
            }

        } catch (Exception exception) {
            LogServer.LogException("MapTrasportHandler: " + exception.getMessage(), exception);
        }
    }

}
