package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.LogServer;
import nro.service.ResourceService;

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
