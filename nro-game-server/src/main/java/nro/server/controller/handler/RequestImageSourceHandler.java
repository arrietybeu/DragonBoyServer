package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.system.LogServer;
import nro.server.network.Message;
import nro.server.network.Session;

@APacketHandler(-111)
public class RequestImageSourceHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            short size = message.reader().readShort();
            for (int i = 0; i < size; i++) {
                var string = message.reader().readUTF();
                System.out.println("size: " + size + " string: " + string);
            }
        } catch (Exception e) {
            LogServer.LogException("Error RequestImageSourceHandler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
