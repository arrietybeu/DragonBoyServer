package nro.service;

import nro.model.item.Item;
import nro.model.map.GameMap;
import nro.model.npc.Npc;
import nro.model.template.map.TileSetTemplate;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.entity.UserInfo;
import nro.repositories.player.PlayerCreator;
import nro.server.config.ConfigDB;
import nro.server.manager.MapManager;
import nro.server.manager.SessionManager;
import nro.server.manager.UserManager;
import nro.server.manager.ItemManager;
import nro.server.manager.resources.PartManager;
import nro.server.Maintenance;
import nro.server.LogServer;
import nro.server.manager.skill.SpeacialSkillManager;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class CommandService {

    private static CommandService instance;

    public CommandService getInstance() {
        if (instance == null) {
            instance = new CommandService();
        }
        return instance;
    }

    public static void ActiveCommandLine() {
        try {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                switch (line) {
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
                    case "session":
                        LogServer.DebugLogic("Session: " + SessionManager.getInstance().getSizeSession());
                        break;
                    case "database":
                        break;
                    case "skill":
//                        CommandService.getSkill();
                        break;
                    case "kick_all":
                        SessionManager.getInstance().kickAllPlayer("Thích thì kick");
                        break;
                    case "close_database":
                        DatabaseConnectionPool.closeConnections();
                        break;
                    case "version_image":
//                        System.out.println("Version image: " + ResourcesManager.getInstance().getDatas().size());
                        break;
                    case "send":
//                        Session session = Client.gI().getSessionById(0);
//                        Resources.getInstance().sendDataImageVersion(session);
                        break;
                    case "part":
//                        for (PartInfo partInfo : PartManager.getInstance().getPartMap().values()) {
//                            LogServer.DebugLogic("Part: " + partInfo.toString());
//                        }
                        PartManager.getInstance().reload();
                        break;
                    case "skill_template":
//                        SkillManager.getInstance().logAllSkills();
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
                        Connection con = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_DYNAMIC);
                        for (int i = 0; i < 1000; i++) {
                            PlayerCreator.getInstance().createPlayer(con, 1, "tuanbeo", (byte) 2, 1);
                        }
                        break;
                    case "tile_set":
                        for (TileSetTemplate tileSetTemplate : MapManager.getInstance().getTileSetTemplates()) {
                            LogServer.DebugLogic(tileSetTemplate.toString());
                        }
                        break;
                    case "data_skill":
//                        DataSkill.SendDataSkill();
                        break;
                    case "create_item":
                        List<Item> items = ItemService.initializePlayerItems((byte) 0);
                        for (var item : items) {
                            System.out.println(item.getJsonOptions());
                        }
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
                            System.out.println("Npc size: " + zone.getNpcs().size());
                            for (Npc npc : zone.getNpcs()) {
                                System.out.println("Npc: " + npc.toString());
                                System.out.println("Npc Name: " + npc.findNameNpcByTemplate());
                            }
                        }
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
