package nro.controller.handler;

import nro.consts.ConstsCmd;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
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
