package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.npc.Npc;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.core.system.ServerService;

@APacketHandler(22)
public class SelectMenuMagicTreeHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var npcId = message.reader().readByte();
            var select = message.reader().readByte();
            // var option = message.reader().readByte();

            if (npcId == 4) {
                Npc npc = player.getArea().getNpcById(npcId);
                npc.openUIConfirm(player, select);
                return;
            }

            ServerService.getInstance().sendHideWaitDialog(player);
        } catch (Exception ex) {
            LogServer.LogException("SelectMenuMagicTreeHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
