package nro.model.npc;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import nro.server.LogServer;

public class NpcFactory {

    private static final Map<Integer, Npc> npcMap = new HashMap<>();

    public static void init(String packageName) {
        try {
            Reflections rf = new Reflections(packageName);
            Set<Class<?>> classes = rf.getTypesAnnotatedWith(ANpcHandler.class);

            for (Class<?> cls : classes) {
                if (Npc.class.isAssignableFrom(cls)) {
                    ANpcHandler annotation = cls.getAnnotation(ANpcHandler.class);
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
                                LogServer.LogException("Error initializing NPC: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error scanning NPCs: " + e.getMessage(), e);
        }
    }

    public static Npc createNpc(int npcId, int status, int mapId, int x, int y, int avatar) {
        try {
            if (npcMap.containsKey(npcId)) {
                return npcMap.get(npcId).cloneNpc(npcId, status, mapId, x, y, avatar);
            }
//            LogServer.LogWarning("Unknown NPC: [" + npcId + "]");
        } catch (Exception e) {
            LogServer.LogException("createNpc error: " + e.getMessage(), e);
        }
        return null;
    }

    public static Npc getNpc(int npcId) {
        return npcMap.get(npcId);
    }

}