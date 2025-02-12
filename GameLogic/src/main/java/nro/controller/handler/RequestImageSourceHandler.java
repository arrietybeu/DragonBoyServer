package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.network.*;
import nro.server.LogServer;

@APacketHandler(-111)
public class RequestImageSourceHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            short id = message.reader().readShort();
//            System.out.println(id);
        } catch (Exception e) {
            LogServer.LogException("Error RequestImageSourceHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
