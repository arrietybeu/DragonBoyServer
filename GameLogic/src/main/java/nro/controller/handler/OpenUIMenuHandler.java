package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.npc.NpcService;

@APacketHandler(33)
public class OpenUIMenuHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var npcId = message.reader().readShort();
            NpcService.getInstance().openMenuNpc(player, npcId);
        } catch (Exception ex) {
            LogServer.LogException("OpenUIMenuHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
