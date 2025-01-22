package nro.controller.handler;

import nro.network.*;
import nro.controller.interfaces.*;
import nro.server.LogServer;
import nro.service.ResourceService;

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
