package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.core.usage.UseItem;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-43)
public class UseItemHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        if (player.getPoints().isDead()) return;
        try {
            var type = message.reader().readByte();
            var where = message.reader().readByte();
            var index = message.reader().readByte();
            short template = -1;
            if (index == -1) {
                template = message.reader().readShort();
            }
            LogServer.DebugLogic("player: " + player.getName() + " status: " + type + " where: " + where + " index: " + index + " template: " + template);
            UseItem.getInstance().useItem(player, type, where, index, template);
        } catch (Exception ex) {
            LogServer.LogException("UseItemHandler: " + ex.getMessage(), ex);
        }
    }
}
