package nro.service;

import lombok.Getter;
import nro.model.player.Player;
import nro.network.Message;
import nro.server.LogServer;

public class ChatService {

    @Getter
    private static final ChatService instance = new ChatService();

    public void chatMap(Player player, String text) {
        try (Message message = new Message(44)) {
            message.writer().writeInt(player.getId());
            message.writer().writeUTF(text);
            player.getArea().sendToAllPlayers(message);
        } catch (Exception ex) {
            LogServer.LogException("Error Service Chat Map: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
