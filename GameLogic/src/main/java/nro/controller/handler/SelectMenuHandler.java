package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.npc.NpcService;

@APacketHandler(32)
public class SelectMenuHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var npcId = message.reader().readShort();
            var select = message.reader().readByte();
            NpcService.getInstance().confirmMenu(player, npcId, select);
        } catch (Exception ex) {
            LogServer.LogException("Error SelectMenuHandler: " + ex.getMessage(), ex);
        }
    }
}
