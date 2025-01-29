package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;

@APacketHandler(-66)
public class GetEffectDataHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
    }
}
