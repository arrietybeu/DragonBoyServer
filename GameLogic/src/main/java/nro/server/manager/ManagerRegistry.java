package nro.server.manager;

import nro.server.manager.resources.PartManager;
import nro.server.manager.resources.ResourcesManager;
import nro.server.manager.skill.SkillManager;
import nro.server.manager.skill.SkillPaintManager;
import nro.server.system.LogServer;
import nro.server.manager.skill.SpeacialSkillManager;

import java.util.ArrayList;
import java.util.List;

public final class ManagerRegistry {

    private static final List<IManager> MANAGERS = new ArrayList<>();

    static {
        MANAGERS.add(NpcManager.getInstance());
        MANAGERS.add(MonsterManager.getInstance());
        MANAGERS.add(ResourcesManager.getInstance());
        MANAGERS.add(PartManager.getInstance());
        MANAGERS.add(MapManager.getInstance());
        MANAGERS.add(SkillManager.getInstance());
        MANAGERS.add(SkillPaintManager.getInstance());
        MANAGERS.add(ItemManager.getInstance());
        MANAGERS.add(CaptionManager.getInstance());
        MANAGERS.add(SpeacialSkillManager.getInstance());
        MANAGERS.add(TaskManager.getInstance());
        MANAGERS.add(GameNotifyManager.getInstance());
        MANAGERS.add(MagicTreeManager.getInstance());
        MANAGERS.add(ItemMapManager.getInstance());
        MANAGERS.add(ShopManager.getInstance());
//        MANAGERS.add(LuaManager.getInstance());
    }

    public static void initAll() {
        long totalStart = System.currentTimeMillis();

        for (IManager manager : MANAGERS) {
            long startTime = System.currentTimeMillis();
            try {
                manager.init();
                long elapsedTime = System.currentTimeMillis() - startTime;
                LogServer.LogInit("Initialized manager class: [" + manager.getClass().getSimpleName() + "] in " + elapsedTime + " ms");
            } catch (Exception e) {
                LogServer.LogException("Error initializing manager: " + manager.getClass().getSimpleName() + " - " + e.getMessage(), e);
            }
        }

        long totalElapsedTime = System.currentTimeMillis() - totalStart;
        LogServer.DebugLogic("All managers initialized in " + totalElapsedTime + " ms");
    }


    public static void reloadAll() {
        for (IManager manager : MANAGERS) {
            try {
                manager.reload();
            } catch (Exception e) {
                LogServer.LogException("Error reloading manager: " + manager.getClass().getSimpleName() + " - " + e.getMessage(), e);
            }
        }
    }

    public static void clearAll() {
        for (IManager manager : MANAGERS) {
            try {
                LogServer.DebugLogic("Clearing manager: " + manager.getClass().getSimpleName());
                manager.clear();
                LogServer.DebugLogic("  - After clear: " + manager.getClass().getSimpleName() + " is cleared");
            } catch (Exception e) {
                LogServer.LogException("Error clearing manager: " + manager.getClass().getSimpleName() + " - " + e.getMessage(), e);
            }
        }
    }

    public static <T extends IManager> void reloadManager(Class<T> managerClass) {
        for (IManager manager : MANAGERS) {
            if (manager.getClass().equals(managerClass)) {
                try {
                    manager.reload();
                    LogServer.DebugLogic("Reloaded manager: " + managerClass.getSimpleName());
                } catch (Exception e) {
                    LogServer.LogException("Error reloading manager: " + managerClass.getSimpleName() + " - " + e.getMessage(), e);
                }
                return;
            }
        }
        LogServer.LogException("Manager not found: " + managerClass.getSimpleName());
    }

}
