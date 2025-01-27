package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.network.*;
import nro.server.LogServer;

@APacketHandler(-111)
public class ImageSourceHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            short id = message.reader().readShort();
        } catch (Exception e) {
            LogServer.LogException("Error ImageSourceHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
