package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.LogServer;
import nro.service.ItemService;

@APacketHandler(-103)
public class ChangeFlagHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            byte action = message.reader().readByte();
            switch (action) {
                case 0: {
                    ItemService.getInstance().sendShowListFlagBag(player);
                    break;
                }
                default: {
                    byte flagType = message.reader().readByte();
                    break;
                }
            }
            System.out.println("changer flag bag action: " + action);
        } catch (Exception ex) {
            LogServer.LogException("ChangeFlagHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
