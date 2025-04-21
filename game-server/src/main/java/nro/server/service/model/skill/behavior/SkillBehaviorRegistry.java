package nro.server.service.model.skill.behavior;

import nro.server.system.LogServer;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SkillBehaviorRegistry {

    private static final Map<Integer, SkillBehavior> behaviorMap = new HashMap<>();

    public static void init(String basePackage) {
        try {
            Reflections reflections = new Reflections(basePackage);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ASkillHandler.class);

            for (Class<?> cls : classes) {
                if (SkillBehavior.class.isAssignableFrom(cls)) {
                    ASkillHandler annotation = cls.getAnnotation(ASkillHandler.class);
                    Constructor<?> constructor = cls.getDeclaredConstructor();
                    SkillBehavior behavior = (SkillBehavior) constructor.newInstance();

                    for (int skillId : annotation.value()) {
                        behaviorMap.put(skillId, behavior);
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error initializing SkillBehaviorRegistry: " + e.getMessage(), e);
        }
    }

    public static SkillBehavior getBehavior(int skillId) {
        return behaviorMap.get(skillId);
    }

}