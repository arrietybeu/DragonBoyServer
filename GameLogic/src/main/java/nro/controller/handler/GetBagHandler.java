package nro.controller.handler;

import nro.model.item.FlagImage;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.manager.ItemManager;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.ItemService;

@APacketHandler(-63)
public class GetBagHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var idFlagBag = message.reader().readShort();
            FlagImage flagImage = ItemManager.getInstance().findFlagImageId(idFlagBag);
            if (flagImage == null) return;
            ItemService.getInstance().sendFlagBagImage(player, flagImage);
        } catch (Exception ex) {
            LogServer.LogException("GetBagHandler: " + ex.getMessage(), ex);
        }
    }
}
