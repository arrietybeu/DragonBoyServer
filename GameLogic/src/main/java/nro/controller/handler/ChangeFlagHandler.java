package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.network.Message;
import nro.network.Session;
import nro.server.LogServer;

@APacketHandler(-103)
public class ChangeFlagHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            byte action = message.reader().readByte();
            if (action != 0) {
                byte flagType = message.reader().readByte();
            }
        } catch (Exception ex) {
            LogServer.LogException("ChangeFlagHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
