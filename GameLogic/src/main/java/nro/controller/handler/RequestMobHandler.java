package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.interfaces.APacketHandler;
import nro.controller.interfaces.IMessageProcessor;
import nro.server.LogServer;

@APacketHandler(11)
public class RequestMobHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            message.reader().readShort();

        } catch (Exception e) {
            LogServer.LogException("Error RequestMobHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
