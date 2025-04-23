package nro.server.controller.handler;

import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.core.system.ResourceService;
import nro.server.system.LogServer;

@APacketHandler(-87)
public class UpdateDataHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            ResourceService.getInstance().createData(session);
        } catch (Exception e) {
            LogServer.LogException("Error UpdateDataHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
