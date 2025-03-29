package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.npc.NpcService;

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
