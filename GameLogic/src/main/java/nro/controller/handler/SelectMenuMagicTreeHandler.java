package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.npc.Npc;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(22)
public class SelectMenuMagicTreeHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var npcId = message.reader().readByte();
            var select = message.reader().readByte();
            var option = message.reader().readByte();

            Npc npc = player.getArea().getNpcById(npcId);
            npc.openUIConFirm(player, select);

        } catch (Exception ex) {
            LogServer.LogException("SelectMenuMagicTreeHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
