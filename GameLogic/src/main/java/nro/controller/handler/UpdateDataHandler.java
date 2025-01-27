package nro.controller.handler;

import nro.network.Message;
import nro.network.Session;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.ResourceService;
import nro.server.LogServer;

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
