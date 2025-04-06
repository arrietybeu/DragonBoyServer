package nro.server.service.model.entity.ai.handler;

import nro.server.service.core.social.ChatService;
import nro.server.service.model.entity.ai.AIStateHandler;
import nro.server.service.model.entity.ai.AbstractAI;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.system.LogServer;

public class ChatEventHandler implements AIStateHandler {

    /// this method is used to chat with the players in the area.

    @Override
    public void handle(AbstractAI ai) {
        try {
            switch (ai) {
                case Boss boss -> ChatService.getInstance().chatMap(ai, boss.getTextChat());
                default -> LogServer.LogException("ChatEventHandler.handle: " + ai.getClass().getSimpleName() + " is not a Boss");
            }
        } catch (Exception exception) {
            LogServer.LogException("MoveEventHandler.entityChat: " + exception.getMessage(), exception);
        }
    }
}
