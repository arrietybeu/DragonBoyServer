package nro.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.core.player.PlayerService;

@APacketHandler(-38)

public class FinishUpdateHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        PlayerService.getInstance().finishUpdateHandler(session);
    }

}
