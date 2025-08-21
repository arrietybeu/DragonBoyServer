package nro.server.services;

import nro.server.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arriety
 */
public class ChatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private ChatService() {
        throw new AssertionError();
    }


    public static void sendChatMessage(String message) {
        if (message == null || message.isEmpty()) {
            LOGGER.warn("Attempted to send an empty chat message.");
            return;
        }

        PacketSendUtility.sendPacketForALLPlayerInArea();
    }
}
