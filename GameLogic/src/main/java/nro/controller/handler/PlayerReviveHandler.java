package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.player.Player;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.core.system.ServerService;

@APacketHandler(-16)
public class PlayerReviveHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        long time = System.currentTimeMillis();
        if (player.getPlayerStatus().getLastTimeLive() + 5000 > time) {
            ServerService.getInstance().sendChatGlobal(player.getSession(), null,
                    String.format("Bạn chỉ có thể hồi sinh tại chỗ sau %d giây nữa.", (player.getPlayerStatus().getLastTimeLive() + 5000 - time) / 1000), false);
            return;
        }
        try {
            if (player.getPlayerCurrencies().subCurrency(1)) {
                if (player.getPlayerPoints().isDead()) {
                    player.getPlayerStatus().setLastTimeLive(time);
                    player.getPlayerPoints().setLive();
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("PlayerReviveHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
