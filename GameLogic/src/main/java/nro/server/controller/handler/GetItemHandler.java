package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.core.usage.UseItem;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-40)
public class GetItemHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var type = message.reader().readByte();
            var index = message.reader().readByte();
//            LogServer.DebugLogic("status: " + type + " index: " + index);
            UseItem.getInstance().getItem(player, type, index);
        } catch (Exception ex) {
            LogServer.LogException("GetItemHandler: " + ex.getMessage(), ex);
        }
    }
}
