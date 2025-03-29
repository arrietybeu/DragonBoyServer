package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.npc.NpcService;

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
