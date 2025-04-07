//package nro.server.service.model.entity.ai;
//
//import nro.server.service.model.entity.ai.handler.*;
//
//import java.util.EnumMap;
//import java.util.Map;
//
//public class AIStateFactory {
//
//    private static final Map<AIState, AIStateHandler> handlers = new EnumMap<>(AIState.class);
//
//    static {
//        handlers.put(AIState.SEARCHING, new SearchingEventHandler());
//        handlers.put(AIState.CHASING, new ChasingEventHandler());
//        handlers.put(AIState.ATTACKING, new AttackingEventHandler());
//        handlers.put(AIState.GO_TO_MAP, new GoToMapEventHandler());
//        handlers.put(AIState.CHAT, new ChatEventHandler());
//        handlers.put(AIState.IDLE, new IdleEventHandler());
//    }
//
//    public static AIStateHandler getHandler(AIState state) {
//        return handlers.get(state);
//    }
//}
