package nro.server.service.model.entity.ai.boss;

import lombok.Getter;
import nro.consts.ConstBoss;
import nro.server.manager.entity.BossManager;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.map.areas.Area;
import nro.server.system.LogServer;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class BossFactory {

    @Getter
    private final static BossFactory instance = new BossFactory();

    private final Map<Integer, Class<? extends Boss>> bossClassMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public void init(String basePackage) {
        try {
            Reflections reflections = new Reflections(basePackage);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ABossHandler.class);

            for (Class<?> clazz : classes) {
                if (Boss.class.isAssignableFrom(clazz)) {
                    ABossHandler annotation = clazz.getAnnotation(ABossHandler.class);
                    bossClassMap.put(annotation.value(), (Class<? extends Boss>) clazz);
                }
            }
//            LogServer.LogWarning("Loaded Boss Types: " + bossClassMap.keySet());
        } catch (Exception e) {
            LogServer.LogException("BossFactory.init error", e);
        }
    }

    public Boss createBoss(int bossId, BossPoints points, BossFashion fashion) {
        try {
            Class<? extends Boss> clazz = bossClassMap.get(bossId);
            if (clazz != null) {
                Constructor<? extends Boss> constructor = clazz.getConstructor(int.class, BossPoints.class, BossFashion.class);
                return constructor.newInstance(bossId, points, fashion);
            } else {
                LogServer.LogWarning("Unknown bossId: " + bossId);
            }
        } catch (Exception e) {
            LogServer.LogException("BossFactory.createBoss error for id: " + bossId, e);
        }
        return null;
    }

    public void trySpawnSpecialBossInArea(Player player, Area area, int bossId) {
        try {
            Boss boss = BossManager.getInstance().getBossById(bossId);
            if (boss == null) {
                LogServer.LogException("BossFactory.trySpawnSpecialBossInArea: Boss not found for id: " + bossId);
                return;
            }
            if (area == null) {
                LogServer.LogException("BossFactory.trySpawnSpecialBossInArea: Area not found for player: " + player.getName());
                return;
            }
            boss.setArea(area);
            AreaService.getInstance().changerMapByShip(boss, area.getMap().getId(), player.getX(), player.getY(), 1, area);
            BossAISystem.getInstance().register(boss);
        } catch (Exception exception) {
            LogServer.LogException("BossFactory.trySpawnSpecialBossInArea error", exception);
        }
    }

}
