package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-78)
public class CheckMoveHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {

    }

}
