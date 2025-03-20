package nro.controller.handler;

import nro.consts.ConstShop;
import nro.consts.ConstsCmd;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.item.Item;
import nro.service.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.system.ServerService;

import nro.service.core.item.ItemFactory;

@APacketHandler(ConstsCmd.KIGUI)
public class ConsignmentShopHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var action = message.reader().readByte();
            switch (action) {
                case ConstShop.BUY -> {
                    var idItem = message.reader().readShort();
                    var moneyType = message.reader().readByte();
                    var money = message.reader().readInt();

                    Item item = ItemFactory.getInstance().createItemOptionsBase(idItem);
                    String name = item.getTemplate().name();
                    if (player.getPlayerInventory().addItemBag(item)) {
                        ServerService.getInstance().sendChatGlobal(session, null, "Nhận thành công " + name, false);
                    }
                }
                default -> {
                    ServerService.getInstance().sendHideWaitDialog(player);
                    LogServer.LogWarning("ConsignmentShopHandler: " + action);
                }
            }

        } catch (Exception e) {
            LogServer.LogException("ConsignmentShopHandler: " + e.getMessage(), e);
        }
    }
}
