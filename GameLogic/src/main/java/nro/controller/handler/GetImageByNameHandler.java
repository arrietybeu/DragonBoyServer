package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.network.Message;
import nro.network.Session;

import java.io.DataInputStream;
import java.io.IOException;

@APacketHandler(66)
public class GetImageByNameHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            DataInputStream stream = message.reader();
            var name = stream.readUTF();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
