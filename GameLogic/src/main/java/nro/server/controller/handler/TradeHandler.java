package nro.server.controller.handler;

import nro.consts.ConstTrade;
import nro.consts.ConstsCmd;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.core.economy.TradeService;
import nro.server.service.core.economy.TradeSession;
import nro.server.service.core.system.ServerService;
import nro.server.service.model.item.Item;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.model.entity.player.Player;

@APacketHandler(ConstsCmd.GIAO_DICH)
public class TradeHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var action = message.reader().readByte();

            Player opponent;
            TradeSession trade;

            switch (action) {
                case ConstTrade.TRANSACTION_REQUEST -> {
                    int opponentId = message.reader().readInt();
                    opponent = player.getArea().getPlayer(opponentId);
                    if (opponent != null) {
                        if (!TradeService.getInstance().requestTrade(player, opponent)) {
                            ServerService.getInstance().sendChatGlobal(session, null, "Vui lòng đợi 1 lát nữa", false);
                        }
                    }
                }
                case ConstTrade.SELECT_ITEM -> {
                    short itemIndex = message.reader().readShort();
                    trade = TradeService.getInstance().getSession(player);
                    Item item = player.getPlayerInventory().getItemsBag().get(itemIndex);
                    if (item != null && item.getTemplate() != null) {
                        trade.addItem(player, item);
                    }
                }
                case ConstTrade.LOCK_TRADE -> TradeService.getInstance().lockTrade(player);
                case ConstTrade.CANCLE_TRADE -> TradeService.getInstance().cancelTrade(player);
            }

        } catch (Exception e) {
            LogServer.LogException("TradeHandler: " + e.getMessage(), e);
            TradeService.getInstance().cancelTrade(session.getPlayer());
        }
    }

}
