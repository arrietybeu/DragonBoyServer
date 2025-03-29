package nro.server.controller.handler;

import nro.server.controller.APacketHandler;
import nro.server.controller.IMessageProcessor;
import nro.server.service.model.entity.player.Player;
import nro.server.network.Message;
import nro.server.network.Session;
import nro.server.system.LogServer;
import nro.server.service.core.system.ResourceService;

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
