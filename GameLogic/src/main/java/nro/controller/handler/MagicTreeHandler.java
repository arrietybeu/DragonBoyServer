package nro.controller.handler;

import nro.service.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.core.npc.NpcService;

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
