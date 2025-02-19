package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-78)
public class CheckMoveHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {

    }

}
