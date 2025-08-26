package nro.server.services;

import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.server_packets.handler.SmChatMap;
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

    public static void sendChatMessage(PositionComponent po, String message) throws RuntimeException {
        try {
            if (message == null || message.isEmpty()) {
                LOGGER.warn("Attempted to send an empty chat message.");
                return;
            }

            System.out.println("ChatService.sendChatMessage: " + message);
            // FIXME viet check message ki doan nafy

            AreaService.getInstance().sendPacketForALLPlayerInArea(po.mapId, po.areaId, new SmChatMap(message));

        } catch (RuntimeException e) {
            LOGGER.error("Error sending chat message: {}", e.getMessage(), e);
            throw e;
        }
    }
}
