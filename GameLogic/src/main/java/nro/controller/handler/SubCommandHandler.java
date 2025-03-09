package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-30)
public class SubCommandHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null)
            return;
        try {
            byte command = message.reader().readByte();
            switch (command) {
                case 63 -> {
                }
                case 16 -> {
                    byte typePotential = message.reader().readByte();
                    short num = message.reader().readShort();
                    player.getPlayerPoints().upPotentialPoint(typePotential, num);
                }
                default -> {
                    var info = "Unknow command -30: [" + command + "] ";
                    LogServer.LogWarning(info);
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("SubCommandHandler: " + ex.getMessage(), ex);
        }
    }

}
