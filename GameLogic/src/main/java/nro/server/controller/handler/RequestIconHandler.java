package nro.server.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.server.service.core.system.ResourceService;

@APacketHandler(-67)
public class RequestIconHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            var id = message.reader().readInt();
            ResourceService.getInstance().sendImageRes(session, id);
        } catch (Exception e) {
            LogServer.LogException("RequestIconHandler error: " + e.getMessage());
        }
    }
}
