package nro.server.service.model.entity.ai.boss;

import lombok.Getter;
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
            LogServer.LogWarning("Loaded Boss Types: " + bossClassMap.keySet());
        } catch (Exception e) {
            LogServer.LogException("BossFactory.init error", e);
        }
    }

    public Boss createBoss(int bossId, BossPoints points, BossFashion fashion, BossSkill skills) {
        try {
            Class<? extends Boss> clazz = bossClassMap.get(bossId);
            if (clazz != null) {
                Constructor<? extends Boss> constructor = clazz.getConstructor(int.class, BossPoints.class, BossFashion.class, BossSkill.class);
                return constructor.newInstance(bossId, points, fashion, skills);
            } else {
                LogServer.LogWarning("Unknown bossId: " + bossId);
            }
        } catch (Exception e) {
            LogServer.LogException("BossFactory.createBoss error for id: " + bossId, e);
        }
        return null;
    }

}
