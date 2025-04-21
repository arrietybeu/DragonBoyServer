package nro.server.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;

@APacketHandler(-41)
public class UpdateCaptionHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
    }

}
