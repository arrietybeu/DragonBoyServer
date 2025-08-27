package nro.server.model.npc;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Arriety
 */
public class NpcFactory {

    private static final Logger log = LoggerFactory.getLogger(NpcFactory.class);

    private static final Map<Integer, Npc> npcMap = new HashMap<>();

    public static void init(String packageName) {
        try {
            Reflections rf = new Reflections(packageName);
            Set<Class<?>> classes = rf.getTypesAnnotatedWith(ANpcData.class);

            for (Class<?> cls : classes) {
                if (Npc.class.isAssignableFrom(cls)) {
                    ANpcData annotation = cls.getAnnotation(ANpcData.class);
                    if (annotation != null) {
                        for (int npcId : annotation.value()) {
                            try {
                                Constructor<?> constructor = cls.getDeclaredConstructor(
                                        int.class, int.class, int.class,
                                        int.class, int.class, int.class
                                );

                                Npc npcInstance = (Npc) constructor.newInstance(npcId, 0, 0, 0, 0, 0);
                                npcMap.put(npcId, npcInstance);
                            } catch (Exception e) {
                                log.error("Error initializing NPC: {}", e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error scanning NPCs: {}", e.getMessage(), e);
        }
    }

    public static Npc createNpc(int npcId, int status, int mapId, int x, int y, int avatar) {
        try {
            if (npcMap.containsKey(npcId)) {
                return npcMap.get(npcId).cloneNpc(npcId, status, mapId, x, y, avatar);
            }
//            LogServer.LogWarning("Unknown NPC: [" + npcId + "]");
        } catch (Exception e) {
            log.error("createNpc error: {}", e.getMessage(), e);
        }
        return null;
    }

    public static Npc getNpc(int npcId) {
        return npcMap.get(npcId);
    }

    public static void clear() {
        npcMap.clear();
    }

}
