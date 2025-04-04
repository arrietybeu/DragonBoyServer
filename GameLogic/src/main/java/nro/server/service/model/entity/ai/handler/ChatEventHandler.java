package nro.server.service.model.entity.ai.handler;

import nro.server.service.core.social.ChatService;
import nro.server.service.model.entity.Entity;
import nro.server.system.LogServer;

public class ChatEventHandler {

    /// this method is used to chat with the players in the area.
    public static void entityChat(Entity entity, String message) {
        try {
            ChatService.getInstance().chatMap(entity, message);
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.entityChat: " + exception.getMessage(), exception);
        }
    }

}
