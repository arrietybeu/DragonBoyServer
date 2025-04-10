package nro.server.controller.handler;

import nro.consts.ConstsCmd;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.service.model.entity.player.Player;
import nro.server.system.LogServer;

@APacketHandler(ConstsCmd.MOB_CAPCHA)
public class CaptchaHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {
        Player player = session.getPlayer();
        if (player == null) return;
        try {
            var ch = message.reader().readChar();
            System.out.println("Captcha: " + ch);
        } catch (Exception exception) {
            LogServer.LogException("CaptchaHandler: " + exception.getMessage(), exception);
        }
    }
}
