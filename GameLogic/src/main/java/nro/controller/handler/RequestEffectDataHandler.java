package nro.controller.handler;

import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.LogServer;
import nro.service.ResourceService;

@APacketHandler(-66)
public class RequestEffectDataHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            short idEffect = message.reader().readShort();
            if (player == null) return;
            ResourceService.getInstance().sendEffectData(player, idEffect);
        } catch (Exception ex) {
            LogServer.LogException("Error Write Message RequestEffectDataHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
