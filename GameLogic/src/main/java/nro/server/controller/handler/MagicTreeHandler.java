package nro.server.controller.handler;

import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.core.npc.NpcService;

@APacketHandler(-34)
public class MagicTreeHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var type = message.reader().readByte();
            switch (type) {
                case 1: {
                    break;
                }
                case 2: {
                    NpcService.getInstance().loadMagicTree(player, 0, null);
                    break;
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("MagicTreeHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
