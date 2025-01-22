package nro.controller.handler;

import nro.controller.interfaces.APacketHandler;
import nro.controller.interfaces.IMessageProcessor;
import nro.network.Message;
import nro.network.Session;

@APacketHandler(42)
public class UserInfoHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {

    }

}
