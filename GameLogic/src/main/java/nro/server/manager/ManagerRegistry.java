package nro.server.manager;

import nro.server.manager.resources.PartManager;
import nro.server.manager.resources.ResourcesManager;
import nro.server.manager.skill.SkillManager;
import nro.server.manager.skill.SkillPaintManager;
import nro.server.LogServer;
import nro.server.manager.skill.SpeacialSkillManager;

import java.util.ArrayList;
import java.util.List;

public class ManagerRegistry {

    private static final List<IManager> MANAGERS = new ArrayList<>();

    static {
        MANAGERS.add(NpcManager.getInstance());
        MANAGERS.add(MonsterManager.getInstance());
        MANAGERS.add(ResourcesManager.getInstance());
        MANAGERS.add(PartManager.getInstance());
        MANAGERS.add(MapManager.getInstance());
        MANAGERS.add(SkillManager.getInstance());
        MANAGERS.add(SkillPaintManager.gI());
        MANAGERS.add(ItemManager.getInstance());
        MANAGERS.add(CaptionManager.getInstance());
        MANAGERS.add(SpeacialSkillManager.getInstance());
        MANAGERS.add(TaskManager.getInstance());
        MANAGERS.add(GameNotifyManager.getInstance());
    }

    public static void initAll() {
        for (IManager manager : MANAGERS) {
            try {
                manager.init();
            } catch (Exception e) {
                LogServer.LogException("Error initializing manager: " + manager.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }
    }

    public static void reloadAll() {
        for (IManager manager : MANAGERS) {
            try {
                manager.reload();
            } catch (Exception e) {
                LogServer.LogException("Error reloading manager: " + manager.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }
    }

    public static void clearAll() {
        for (IManager manager : MANAGERS) {
            try {
                manager.clear();
            } catch (Exception e) {
                LogServer.LogException("Error clearing manager: " + manager.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }
    }
}
