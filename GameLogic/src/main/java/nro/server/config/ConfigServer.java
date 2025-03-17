/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.server.config;

import java.net.InetAddress;

/**
 * @author Arriety
 */
public final class ConfigServer {

    public static String IP;
    public static String LINK_IP_PORT;
    public static int PORT = 14445;

    public static int MAX_SESSIONS = 1000;
    public static int MAX_SESSIONS_PER_IP = 10;

    public static byte VERSION_DATA = 1;
    public static byte VERSION_MAP = 1;
    public static byte VERSION_SKILL = 1;
    public static byte VERSION_ITEM = 1;
    public static byte EXP_RATE = 1;

    public static String VERSION_CLIENT = "2.4.3";

    public static boolean DEBUG = false;

    public static boolean IS_OPEN_UI_LOGBUG = true;

    /**
     * nếu client gửi quá số lượng msg quy định thì sẽ kick session
     */
    public static int MAX_MESSAGES_PER_5_SECONDS = 120;

    public static final String BACKUP_FOLDER_PATH = "\\backup";
    public static final String MYSQL_DUMP_PATH = "";

    public static final String SCRIPT_FOLDER = "scripts/";

    static {
        try {
            IP = InetAddress.getLocalHost().getHostAddress();
//            IP = "206.189.150.19";
        } catch (Exception e) {
            IP = "127.0.0.1";
            System.err.println("khong tim thay ip may chuyen ip default: " + IP);
        }
        LINK_IP_PORT = String.format("Arriety:%s:14445", IP);
    }
}
