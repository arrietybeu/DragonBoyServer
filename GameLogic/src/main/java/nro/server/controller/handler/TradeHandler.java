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

            LogServer.LogInfo("TradeHandler: " + player.getName() + " - " + action);

            TradeService tradeService = TradeService.getInstance();
            switch (action) {

                case ConstTrade.TRANSACTION_REQUEST -> {
                    int opponentId = message.reader().readInt();
                    opponent = this.getPlayerById(player, opponentId);
                    if (opponent != null) {
                        if (!tradeService.requestTrade(player, opponent)) {
                            ServerService.getInstance().sendChatGlobal(session, null, "Vui lòng đợi 1 lát nữa", false);
                        }
                    }
                }

                case ConstTrade.TRANSACTION_ACCEPT -> {
                    int opponentId = message.reader().readInt();
                    opponent = this.getPlayerById(player, opponentId);
                    if (opponent == null) return;
                    tradeService.acceptTrade(player, opponent);
                }

                case ConstTrade.SELECT_ITEM -> {
                    short itemIndex = message.reader().readByte();
                    int quantity = message.reader().readInt();
                    if (itemIndex == -1) {
                        tradeService.addGoldToTrade(player, quantity);
                    } else {
                        tradeService.addItemToTrade(player, itemIndex, quantity);
                    }
                }

                case ConstTrade.CANCLE_TRADE -> tradeService.cancelTrade(player);

                case ConstTrade.LOCK_TRADE -> tradeService.lockTrade(player);

                case ConstTrade.SUSSCESS_TRADE -> tradeService.doneTrade(player);
            }

        } catch (Exception e) {
            LogServer.LogException("TradeHandler: " + e.getMessage(), e);
            TradeService.getInstance().cancelTrade(session.getPlayer());
        }
    }

    private Player getPlayerById(Player player, int id) {
        return player.getArea().getPlayer(id);
    }

}
