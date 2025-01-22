package nro.service;

import nro.data.DataSkill;
import nro.model.template.map.TileSetTemplate;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.entity.UserInfo;
import nro.repositories.player.PlayerCreator;
import nro.server.config.ConfigDB;
import nro.server.manager.MapManager;
import nro.server.manager.SessionManager;
import nro.server.manager.UserManager;
import nro.server.manager.item.ItemManager;
import nro.server.manager.resources.PartManager;
import nro.server.manager.skill.SkillManager;
import nro.server.Maintenance;
import nro.server.LogServer;

import java.sql.Connection;
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
                    case "baotri":
                        Maintenance.getInstance().active(5);
                        break;
                    case "thread":
                        LogServer.DebugLogic("Thread: " + Thread.activeCount());
                        break;
                    case "session":
                        LogServer.DebugLogic("Session: " + SessionManager.gI().getSizeSession());
                        break;
                    case "database":
                        break;
                    case "skill":
//                        CommandService.getSkill();
                        break;
                    case "kick_all":
                        SessionManager.gI().kickAllPlayer("Thích thì kick");
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
                        SkillManager.getInstance().logAllSkills();
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
                        DataSkill.SendDataSkill();
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
