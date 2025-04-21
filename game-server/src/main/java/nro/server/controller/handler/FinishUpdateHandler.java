package nro.server.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.core.player.PlayerService;

@APacketHandler(-38)

public class FinishUpdateHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        PlayerService.getInstance().finishUpdateHandler(session);
    }

}
