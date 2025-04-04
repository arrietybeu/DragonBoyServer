package nro.server.service.model.entity.ai;

import nro.server.service.model.entity.ai.handler.AttackingEventHandler;
import nro.server.service.model.entity.ai.handler.ChasingEventHandler;
import nro.server.service.model.entity.ai.handler.SearchingEventHandler;

import java.util.EnumMap;
import java.util.Map;

public class AIStateManager {

    private static final Map<AIState, AIStateHandler> handlers = new EnumMap<>(AIState.class);

    static {
        handlers.put(AIState.SEARCHING, new AttackingEventHandler());
        handlers.put(AIState.CHASING, new ChasingEventHandler());
        handlers.put(AIState.ATTACKING, new SearchingEventHandler());
        handlers.put(AIState.IDLE, ai -> {
        });
    }

    public static AIStateHandler getHandler(AIState state) {
        return handlers.get(state);
    }
}
