package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.ResourceService;
import nro.server.LogServer;

@APacketHandler(-74)
public class GetImageSourceHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            ResourceService.getInstance().downloadResources(session, message);
        } catch (Exception e) {
            LogServer.LogException("Error GetImageSourceHandler: " + e.getMessage());
        }
    }
}
