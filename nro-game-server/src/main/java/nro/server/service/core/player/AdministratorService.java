package nro.server.service.core.player;

import nro.consts.ConstsCmd;
import nro.server.network.Message;
import nro.server.realtime.system.player.TradeSystem;
import nro.server.service.core.economy.TradeSession;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

import java.io.DataOutputStream;
import java.util.Map;

public class AdministratorService {

    private static final class SingletonHolder {
        private static final AdministratorService instance = new AdministratorService();
    }

    public static AdministratorService getInstance() {
        return SingletonHolder.instance;
    }

    public void sendListUITrade(Player admin) {
        TradeSystem tradeSystem = TradeSystem.getInstance();
        try (Message message = new Message(ConstsCmd.OPEN_UI_ZONE)) {

            DataOutputStream writer = message.writer();
            Map<Integer, TradeSession> tradeSessions = tradeSystem.getTradeSessions();

            writer.writeByte(tradeSystem.size());

            for (TradeSession trade : tradeSessions.values()) {
                writer.writeByte(trade.getIdTrade());// id trade
                writer.writeByte(1);// mau vang
                writer.writeByte(1);// const pl
                writer.writeByte(1);// max pl
                writer.writeByte(1);// max pl
                writer.writeUTF(trade.getPlayer1().getName());
                writer.writeInt(1);// top
                writer.writeUTF(trade.getPlayer2().getName());
                writer.writeInt(1);// top
            }
            admin.sendMessage(message);
        } catch (Exception exception) {
            LogServer.LogException("AdministratorService {sendListUITrade}: " + exception.getMessage(), exception);
        }
    }

}
