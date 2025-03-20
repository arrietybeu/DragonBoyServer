package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.usage.UseItemService;

@APacketHandler(-43)
public class UseItemHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var type = message.reader().readByte();
            var where = message.reader().readByte();
            var index = message.reader().readByte();
            short template = -1;
            if (index == -1) {
                template = message.reader().readShort();
            }
            LogServer.DebugLogic("player: " + player.getName() + " status: " + type + " where: " + where + " index: " + index + " template: " + template);
            UseItemService.getInstance().useItem(player, type, where, index, template);
        } catch (Exception ex) {
            LogServer.LogException("UseItemHandler: " + ex.getMessage(), ex);
        }
    }
}
