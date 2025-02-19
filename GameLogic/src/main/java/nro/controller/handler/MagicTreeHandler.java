package nro.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;

@APacketHandler(-34)
public class MagicTreeHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {

    }
}
