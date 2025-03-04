package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.LogServer;

@APacketHandler(-103)
public class ChangeFlagHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            byte action = message.reader().readByte();
            byte index = -1;
            if (action == 0) {
                player.changeFlag(action, index);
            } else {
                index = message.reader().readByte();
                player.changeFlag(action, index);
            }

        } catch (Exception ex) {
            LogServer.LogException("ChangeFlagHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
