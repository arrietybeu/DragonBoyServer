package nro.server.service.model.entity.ai.boss;

import lombok.Getter;
import nro.server.manager.entity.BossManager;
import nro.server.realtime.system.boss.BossAISystem;
import nro.server.service.core.map.AreaService;
import nro.server.service.model.entity.Points;
import nro.server.service.model.entity.ai.AIState;
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

    public Boss createBoss(int bossId, BossFashion fashion) {
        try {
            Class<? extends Boss> clazz = bossClassMap.get(bossId);
            if (clazz != null) {
                Constructor<? extends Boss> constructor = clazz.getConstructor(int.class, BossFashion.class);
                return constructor.newInstance(bossId, fashion);
            } else {
                LogServer.LogWarning("Unknown bossId: " + bossId);
            }
        } catch (Exception e) {
            LogServer.LogException("BossFactory.createBoss error for id: " + bossId, e);
        }
        return null;
    }

    public void trySpawnSpecialBossInAreaToPointsPlayer(Player player, Area area, int x, int y, int bossId) {
        try {
            if (area == null) {
                LogServer.LogException("BossFactory.trySpawnSpecialBossInArea: Area is null for boss id: " + bossId);
                return;
            }

            Boss boss = this.createBossFromTemplate(bossId, x, y, area);
            if (boss == null) return;
            boss.setEntityTarget(player);

            Points points = boss.getPoints();

            long newHp = points.getMaxHP() + player.getPoints().getCurrentDamage() * 3;
            long newDamage = points.getCurrentDamage() + player.getPoints().getMaxHP() * 3;

            boss.getPoints().setMaxHP(newHp);
            boss.getPoints().setCurrentHp(newHp);
            boss.getPoints().setCurrentDamage(newDamage);

            BossAISystem.getInstance().register(boss);
        } catch (Exception e) {
            LogServer.LogException("BossFactory.trySpawnSpecialBossInArea error", e);
        }
    }

    public void trySpawnSpecialBossInArea(Player player, Area area, int x, int y, int bossId) {
        try {
            if (area == null) {
                LogServer.LogException("BossFactory.trySpawnSpecialBossInArea: Area is null for boss id: " + bossId);
                return;
            }

            Boss boss = this.createBossFromTemplate(bossId, x, y, area);
            if (boss == null) return;
            boss.setEntityTarget(player);

            long newHp = player.getPoints().getMaxHP() * 2;

            boss.getPoints().setMaxHP(newHp);
            boss.getPoints().setCurrentHp(newHp);

            BossAISystem.getInstance().register(boss);
        } catch (Exception e) {
            LogServer.LogException("BossFactory.trySpawnSpecialBossInArea error", e);
        }
    }

    public Boss createBossFromTemplate(int bossId, int x, int y, Area area) {
        try {
            Boss template = BossManager.getInstance().getTemplateById(bossId);
            if (template == null) {
                LogServer.LogException("BossFactory.createBossFromTemplate: Template not found for id: " + bossId);
                return null;
            }

            BossFashion fashion = (BossFashion) template.getFashion().copy();

            if (fashion == null) {
                LogServer.LogException("Copy failed for Boss components: fashion is null (bossId = " + bossId + ")");
                return null;
            }

            Boss boss = this.createBoss(template.getId(), fashion);
            if (boss == null) {
                LogServer.LogException("Failed to create boss clone for id: " + bossId);
                return null;
            }
            BossPoints points = (BossPoints) template.getPoints().copy(boss);
            BossSkill skills = (BossSkill) template.getSkills().copy(boss);
            boss.setName(template.getName());
            boss.setGender(template.getGender());
            boss.setSkills(skills);
            boss.setPoints(points);
            boss.setRespawnTime(template.getRespawnTime());
            boss.setAfkTimeout(template.getAfkTimeout());
            boss.setAutoDespawn(template.isAutoDespawn());
            boss.setTextChat(template.getTextChat());
            boss.setMapsId(template.getMapsId().clone());
            boss.setSpawnType(template.getSpawnType());
            boss.setX((short) x);
            boss.setY((short) y);
            boss.setArea(area);
            boss.setController(template.getController());
            boss.setTypeLeaveMap(template.getTypeLeaveMap());
            return boss;
        } catch (Exception e) {
            LogServer.LogException("BossFactory.createBossFromTemplate error", e);
            return null;
        }
    }

}
