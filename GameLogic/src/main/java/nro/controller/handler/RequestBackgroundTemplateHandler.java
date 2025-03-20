package nro.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.core.system.ResourceService;
import nro.server.LogServer;

@APacketHandler(-32)
public class RequestBackgroundTemplateHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            var id = message.reader().readShort();
            ResourceService.getInstance().sendDataBackgroundMap(session, id);
        } catch (Exception e) {
            LogServer.LogException("Error in RequestBackgroundTemplateHandler: " + e.getMessage());
        }
    }
}
