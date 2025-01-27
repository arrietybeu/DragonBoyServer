package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;

@APacketHandler(-63)
public class GetBagHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {

    }
}
