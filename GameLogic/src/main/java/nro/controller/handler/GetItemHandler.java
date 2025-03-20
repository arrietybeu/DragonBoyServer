package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.UseItemService;

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
            UseItemService.getInstance().getItem(player, type, index);
        } catch (Exception ex) {
            LogServer.LogException("GetItemHandler: " + ex.getMessage(), ex);
        }
    }
}
