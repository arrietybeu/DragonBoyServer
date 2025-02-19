package nro.controller.handler;

import nro.consts.ConstsCmd;
import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.server.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-30)
public class SubCommandHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            byte command = message.reader().readByte();
            switch (command) {
                case 63:
                    break;
                default:
                    var info = "Unknow command -30: [" + command + "] ";
                    LogServer.LogWarning(info);
                    break;
            }
        } catch (Exception ex) {
            LogServer.LogException("SubCommandHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
