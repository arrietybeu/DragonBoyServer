package nro.server.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.core.system.ResourceService;
import nro.server.system.LogServer;

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
