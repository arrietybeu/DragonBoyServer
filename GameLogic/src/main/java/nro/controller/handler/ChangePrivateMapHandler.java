package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.network.Message;
import nro.network.Session;

@APacketHandler(-33)
public class ChangePrivateMapHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {

    }
}
