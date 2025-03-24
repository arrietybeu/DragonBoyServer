package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;
import nro.service.core.system.ResourceService;

import java.io.DataInputStream;
import java.io.IOException;

@APacketHandler(66)
public class RequestImageByNameHandler implements IMessageProcessor {

    @Override
    public void process(Session session, Message message) {
        try {
            Player player = session.getPlayer();
            if (player == null) return;
            DataInputStream stream = message.reader();
            var name = stream.readUTF();
            ResourceService.getInstance().sendImageByName(player, name);
        } catch (IOException ex) {
            LogServer.LogException("Error Read Message RequestImageByNameHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
