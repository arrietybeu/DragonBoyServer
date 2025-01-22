package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.interfaces.APacketHandler;
import nro.controller.interfaces.IMessageProcessor;
import nro.service.ResourceService;
import nro.server.LogServer;

@APacketHandler(-32)
public class BackgroundTemplateHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            var id = message.reader().readShort();
            ResourceService.getInstance().sendDataBackgroundMap(session, id);
        } catch (Exception e) {
            LogServer.LogException("Error in BackgroundTemplateHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
