package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.interfaces.APacketHandler;
import nro.controller.interfaces.IMessageProcessor;

@APacketHandler(-34)
public class MagicTreeHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {

    }
}
