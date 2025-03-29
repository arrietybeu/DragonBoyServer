package nro.server.controller.handler;

import nro.consts.ConstsCmd;
import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(ConstsCmd.THACHDAU)
public class SelectChanllengeHandler implements IMessageProcessor {
    @Override
    public void process(Session session, Message message) {
        try {
            var id = message.reader().readInt();
        } catch (Exception e) {
            LogServer.LogException("SelectChanllengeHandler: " + e.getMessage(), e);
        }
    }
}
