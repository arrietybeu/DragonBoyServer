package nro.server.service.core.system;

import nro.server.service.core.system.controller.MapViewerController;
import nro.server.service.model.map.GameMap;
import nro.server.service.model.map.decorates.BackgroudEffect;
import nro.server.service.model.template.CaptionTemplate;
import nro.commons.database.DatabaseFactory;
import nro.server.service.model.template.entity.UserInfo;
import nro.server.service.repositories.player.PlayerCreator;
import nro.server.config.ConfigDB;
import nro.server.manager.*;
import nro.server.manager.resources.PartManager;
import nro.server.system.Maintenance;
import nro.server.system.LogServer;
import nro.server.manager.resources.ResourcesManager;
import nro.server.manager.skill.SpeacialSkillManager;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class CommandService {

    public static void ActiveCommandLine() {
        try {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                switch (line) {
                    case "controller": {
                        MapViewerController mapViewerController = new MapViewerController();
                        break;
                    }
                    case "noi_tai":
                        SpeacialSkillManager skillManager = SpeacialSkillManager.getInstance();
                        for (var option : skillManager.getNamec()) {
                            System.out.println(option.getName());
                        }
                        break;
                    case "baotri":
                        Maintenance.getInstance().active(5);
                        break;
                    case "thread":
                        LogServer.DebugLogic("Thread: " + Thread.activeCount());
                        break;
                    case "quantity_thread":
                        var num = Runtime.getRuntime().availableProcessors(); // so luong cpu
                        LogServer.DebugLogic("So luong cpu: " + num);
                        break;
                    case "session":
                        LogServer.DebugLogic("Session: " + SessionManager.getInstance().getSizeSession());
                        break;
                    case "database":
                        break;
                    case "skill":
                        // CommandService.getSkill();
                        break;
                    case "kick_all":
                        SessionManager.getInstance().kickAllPlayer("Thích thì kick");
                        break;
                    case "close_database":
                        DatabaseFactory.closeAll();
                        break;
                    case "version_image":
                        // System.out.println("Version image: " +
                        // ResourcesManager.getInstance().getDatas().size());
                        break;
                    case "send":
                        // Session session = Client.gI().getSessionById(0);
                        // Resources.getInstance().sendDataImageVersion(session);
                        break;
                    case "part":
                        // for (PartInfo partInfo : PartManager.getInstance().getPartMap().values()) {
                        // LogServer.DebugLogic("Part: " + partInfo.toString());
                        // }
                        PartManager.getInstance().reload();
                        break;
                    case "skill_template":
                        // SkillManager.getInstance().logAllSkills();
                        break;
                    case "item_template":
                        ItemManager.getInstance().logItemTemplate();
                        break;
                    case "item_head":
                        ItemManager.getInstance().logItemArrHead2Fr();
                        break;
                    case "user":
                        for (UserInfo user : UserManager.getInstance().getAllUsers().values()) {
                            LogServer.DebugLogic("User: " + user.getId());
                        }
                        break;
                    case "create":
                        Connection con = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC);
                        for (int i = 0; i < 1000; i++) {
                            PlayerCreator.getInstance().createPlayer(con, 1, "tuanbeo", (byte) 2, 1);
                        }
                        break;
                    case "data_skill":
                        // DataSkill.SendDataSkill();
                        break;
                    case "create_item":
                        // List<Item> items = ItemFactory.getInstance().initializePlayerItems((byte) 0);
                        // for (var item : items) {
                        // System.out.println(item.getJsonOptions());
                        // }
                        break;
                    case "item_bg":
                        for (int i = 0; i < 100; i++) {
                            String filePath = "C:\\Users\\Win Val\\Desktop\\ProjectServer\\resources\\louisgoku\\map\\item_bg_map_data\\" + i;
                            try (DataInputStream reader = new DataInputStream(new FileInputStream(filePath))) {
                                short num6 = reader.readShort();
                                GameMap map = MapManager.getInstance().findMapById((short) i);
                                System.out.println("Map name: " + map.getName() + " id: " + i + " size: " + num6);
                                for (int m = 0; m < num6; m++) {
                                    short id = reader.readShort();
                                    short x = reader.readShort();
                                    short y = reader.readShort();
                                    System.out.println("id: " + id + " x: " + x + " y: " + y);
                                }
                            } catch (IOException e) {
                                System.out.println("Map deo co: " + i);
                            }
                        }
                        break;
                    case "zone":
                        GameMap map = MapManager.getInstance().findMapById((short) 0);
                        System.out.println("Map name: " + map.getName());
                        System.out.println("Zone size: " + map.getAreas().size());
                        for (var zone : map.getAreas()) {
                            System.out.println("Zone id: " + zone.getId());
                            System.out.println("Npc size: " + zone.getNpcList().size());
                            // for (Npc npc : zone.getNpcs()) {
                            // System.out.println("Npc: " + npc.toString());
                            // System.out.println("Npc Name: " + npc.findNameNpcByTemplate());
                            // }
                            // for (var monster : zone.getMonsters().values()) {
                            // System.out.println("Monster: " + monster.toString());
                            // System.out.println("Monster Name: " + monster.findNameMonsterByTemplate());
                            // }
                        }
                        break;
                    case "task":
                        TaskManager taskManager = TaskManager.getInstance();
                        System.out.println(taskManager.getTaskMainById(0).getSubNameList().get(0).toString());
                        break;
                    case "caption":
                        List<CaptionTemplate.CaptionLevel> captionLevels = CaptionManager.getInstance().getCaptionLevelsByGender((byte) 2);
                        for (var captionLevel : captionLevels) {
                            System.out.println(captionLevel.name());
                        }
                        break;
                    case "eff_map":
                        try {
                            GameMap gameMap = MapManager.getInstance().findMapById(0);
                            List<BackgroudEffect> backgroudEffects = gameMap.getBackgroundEffects();
                            System.out.println(backgroudEffects.size());
                            for (var back : backgroudEffects) {
                                System.out.println(back.key() + " - " + back.value());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        break;
                    case "addPlayerInZone":
                        break;
                    case "eff":
                        ResourcesManager manager = ResourcesManager.getInstance();
                        var maps = manager.getEffectData().get(1);
                        System.out.println(maps.get(1).getImgInfo()[1].toString());
                        break;
                    case "reload_map":
                        ManagerRegistry.reloadManager(MapManager.class);
                        break;
                    case "check_user_name":
                        UserManager.getInstance().checkUserName();
                        break;
                    default:
                        LogServer.DebugLogic("Command not found: [" + line + "]");
                        break;
                }
            }
        } catch (Exception e) {
            LogServer.LogException("Error ActiveCommandLine: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
