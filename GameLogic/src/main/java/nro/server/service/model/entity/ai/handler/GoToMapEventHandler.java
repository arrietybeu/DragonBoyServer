//package nro.server.service.model.entity.ai.handler;
//
//import nro.server.manager.MapManager;
//import nro.server.service.core.map.AreaService;
//import nro.server.service.model.entity.ai.AIState;
//import nro.server.service.model.entity.ai.AIStateHandler;
//import nro.server.service.model.entity.ai.AbstractAI;
//import nro.server.service.model.entity.ai.boss.Boss;
//import nro.server.service.model.map.GameMap;
//import nro.server.service.model.map.areas.Area;
//import nro.server.system.LogServer;
//import nro.utils.Util;
//
//public class GoToMapEventHandler implements AIStateHandler {
//    @Override
//    public void handle(AbstractAI ai) {
//        try {
//            Boss boss = (Boss) ai;
//            if (boss == null) return;
//
//            if (boss.isInState(AIState.GO_TO_MAP) || boss.getArea() == null && !boss.isBossInMap()) {
//                int mapId = boss.getMapsId()[Util.nextInt(0, boss.getMapsId().length)];
//                GameMap mapNew = MapManager.getInstance().findMapById(mapId);
//                if (mapNew == null) return;
//                Area newArea = mapNew.getArea(-1, boss);
//                AreaService.getInstance().changerMapByShip(boss, mapNew.getId(), boss.getX(), boss.getY(), 1, newArea);
//                boss.setBossInMap(true);
//                boss.setState(AIState.CHAT);
//            }
//
//        } catch (Exception ex) {
//            LogServer.LogException("GoToMapEventHandler.handle: " + ex.getMessage(), ex);
//        }
//    }
//}
