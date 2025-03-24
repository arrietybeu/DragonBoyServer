package nro.controller.handler;

import nro.consts.ConstsCmd;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.service.model.entity.player.Player;

@APacketHandler(ConstsCmd.GIAO_DICH)
public class TradeHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var action = message.reader().readByte();

            switch (action) {

            }

        } catch (Exception e) {
            LogServer.LogException("TradeHandler: " + e.getMessage(), e);
        }
    }
}
