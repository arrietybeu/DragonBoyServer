package nro.controller.handler;

import nro.controller.APacketHandler;
import nro.controller.IMessageProcessor;
import nro.model.player.Player;
import nro.network.Message;
import nro.network.Session;
import nro.server.LogServer;
import nro.service.ChatService;

import java.io.DataInputStream;

@APacketHandler(44)
public class ChatMapHandler implements IMessageProcessor {

    private static final int MAX_MESSAGE_LENGTH = 200;

    @Override
    public void process(Session session, Message message) {
        if (session == null) {
            LogServer.LogException("ChatMapHandler: session null");
            return;
        }

        try {
            String text;
            try (DataInputStream reader = message.reader()) {
                text = reader.readUTF().trim();
            }

            if (text.isEmpty()) {
                LogServer.LogException("ChatMapHandler: Tin nhắn trống từ " + session.getPlayer().getName());
                return;
            }

            if (text.length() > MAX_MESSAGE_LENGTH) {
                text = text.substring(0, MAX_MESSAGE_LENGTH);
            }

            text = sanitizeMessage(text);

            Player player = session.getPlayer();
            if (player == null) {
                LogServer.LogException("ChatMapHandler: Không tìm thấy Player trong Session.");
                return;
            }

            ChatService.getInstance().chatMap(player, text);
            ChatService.getInstance().commandForAdmins(player, text);
        } catch (Exception ex) {
            LogServer.LogException("Error ChatMapHandler: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String sanitizeMessage(String message) {
        return message.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "");
    }
}
