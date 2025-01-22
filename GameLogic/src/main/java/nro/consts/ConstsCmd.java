/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Arriety
 */
@SuppressWarnings("ALL")
public final class ConstsCmd {

    private static final Map<Byte, String> msgMap = new HashMap<>();

    public static String getMessageName(byte id) {
        return msgMap.getOrDefault(id, "tên không xác định");
    }

    public static void addListMsg() {
        msgMap.put(CMD_EXTRA_BIG, "CMD_EXTRA_BIG");
        msgMap.put(CMD_EXTRA, "CMD_EXTRA");
        msgMap.put(EXTRA_LINK, "EXTRA_LINK");
        msgMap.put(LOGIN, "LOGIN");
        msgMap.put(REGISTER, "REGISTER");
        msgMap.put(CLIENT_INFO, "CLIENT_INFO");
        msgMap.put(SEND_SMS, "SEND_SMS");
        msgMap.put(REGISTER_IMAGE, "REGISTER_IMAGE");
        msgMap.put(MESSAGE_TIME, "MESSAGE_TIME");
        msgMap.put(LOGOUT, "LOGOUT");
        msgMap.put(SELECT_PLAYER, "SELECT_PLAYER");
        msgMap.put(CREATE_PLAYER, "CREATE_PLAYER");
        msgMap.put(DELETE_PLAYER, "DELETE_PLAYER");
        msgMap.put(UPDATE_VERSION, "UPDATE_VERSION");
        msgMap.put(UPDATE_MAP, "UPDATE_MAP");
        msgMap.put(UPDATE_SKILL, "UPDATE_SKILL");
        msgMap.put(UPDATE_ITEM, "UPDATE_ITEM");
        msgMap.put(REQUEST_SKILL, "REQUEST_SKILL");
        msgMap.put(REQUEST_MAPTEMPLATE, "REQUEST_MAPTEMPLATE");
        msgMap.put(REQUEST_MOB_TEMPLATE, "REQUEST_MOB_TEMPLATE");
        msgMap.put(UPDATE_TYPE_PK, "UPDATE_TYPE_PK");
        msgMap.put(PLAYER_ATTACK_PLAYER, "PLAYER_ATTACK_PLAYER");
        msgMap.put(PLAYER_VS_PLAYER, "PLAYER_VS_PLAYER");
        msgMap.put(CLAN_PARTY, "CLAN_PARTY");
        msgMap.put(CLAN_INVITE, "CLAN_INVITE");
        msgMap.put(CLAN_REMOTE, "CLAN_REMOTE");
        msgMap.put(CLAN_LEAVE, "CLAN_LEAVE");
        msgMap.put(CLAN_DONATE, "CLAN_DONATE");
        msgMap.put(CLAN_MESSAGE, "CLAN_MESSAGE");
        msgMap.put(CLAN_UPDATE, "CLAN_UPDATE");
        msgMap.put(CLAN_INFO, "CLAN_INFO");
        msgMap.put(CLAN_JOIN, "CLAN_JOIN");
        msgMap.put(CLAN_MEMBER, "CLAN_MEMBER");
        msgMap.put(CLAN_SEARCH, "CLAN_SEARCH");
        msgMap.put(CLAN_CREATE_INFO, "CLAN_CREATE_INFO");
        msgMap.put(CLIENT_OK, "CLIENT_OK");
        msgMap.put(CLIENT_OK_INMAP, "CLIENT_OK_INMAP");
        msgMap.put(UPDATE_VERSION_OK, "UPDATE_VERSION_OK");
        msgMap.put(INPUT_CARD, "INPUT_CARD");
        msgMap.put(CLEAR_TASK, "CLEAR_TASK");
        msgMap.put(CHANGE_NAME, "CHANGE_NAME");
        msgMap.put(UPDATE_PK, "UPDATE_PK");
        msgMap.put(CREATE_CLAN, "CREATE_CLAN");
        msgMap.put(CONVERT_UPGRADE, "CONVERT_UPGRADE");
        msgMap.put(INVITE_CLANDUN, "INVITE_CLANDUN");
        msgMap.put(NOT_USEACC, "NOT_USEACC");
        msgMap.put(ME_LOAD_ACTIVE, "ME_LOAD_ACTIVE");
        msgMap.put(ME_ACTIVE, "ME_ACTIVE");
        msgMap.put(ME_UPDATE_ACTIVE, "ME_UPDATE_ACTIVE");
        msgMap.put(ME_OPEN_LOCK, "ME_OPEN_LOCK");
        msgMap.put(ITEM_SPLIT, "ITEM_SPLIT");
        msgMap.put(ME_CLEAR_LOCK, "ME_CLEAR_LOCK");
        msgMap.put(GET_IMG_BY_NAME, "GET_IMG_BY_NAME");
        msgMap.put(ME_LOAD_ALL, "ME_LOAD_ALL");
        msgMap.put(ME_LOAD_CLASS, "ME_LOAD_CLASS");
        msgMap.put(ME_LOAD_SKILL, "ME_LOAD_SKILL");
        msgMap.put(ME_LOAD_INFO, "ME_LOAD_INFO");
        msgMap.put(ME_LOAD_HP, "ME_LOAD_HP");
        msgMap.put(ME_LOAD_MP, "ME_LOAD_MP");
        msgMap.put(PLAYER_LOAD_ALL, "PLAYER_LOAD_ALL");
        msgMap.put(PLAYER_SPEED, "PLAYER_SPEED");
        msgMap.put(PLAYER_LOAD_LEVEL, "PLAYER_LOAD_LEVEL");
        msgMap.put(PLAYER_LOAD_VUKHI, "PLAYER_LOAD_VUKHI");
        msgMap.put(PLAYER_LOAD_AO, "PLAYER_LOAD_AO");
        msgMap.put(PLAYER_LOAD_QUAN, "PLAYER_LOAD_QUAN");
        msgMap.put(PLAYER_LOAD_BODY, "PLAYER_LOAD_BODY");
        msgMap.put(PLAYER_LOAD_HP, "PLAYER_LOAD_HP");
        msgMap.put(PLAYER_LOAD_LIVE, "PLAYER_LOAD_LIVE");
        msgMap.put(GOTO_PLAYER, "GOTO_PLAYER");
        msgMap.put(POTENTIAL_UP, "POTENTIAL_UP");
        msgMap.put(SKILL_UP, "SKILL_UP");
        msgMap.put(BAG_SORT, "BAG_SORT");
        msgMap.put(BOX_SORT, "BOX_SORT");
        msgMap.put(BOX_COIN_OUT, "BOX_COIN_OUT");
        msgMap.put(REQUEST_ITEM, "REQUEST_ITEM");
        msgMap.put(ME_ADD_SKILL, "ME_ADD_SKILL");
        msgMap.put(ME_UPDATE_SKILL, "ME_UPDATE_SKILL");
        msgMap.put(GET_PLAYER_MENU, "GET_PLAYER_MENU");
        msgMap.put(PLAYER_MENU_ACTION, "PLAYER_MENU_ACTION");
        msgMap.put(SAVE_RMS, "SAVE_RMS");
        msgMap.put(LOAD_RMS, "LOAD_RMS");
        msgMap.put(USE_BOOK_SKILL, "USE_BOOK_SKILL");
        msgMap.put(LOCK_INVENTORY, "LOCK_INVENTORY");
        msgMap.put(CHANGE_FLAG, "CHANGE_FLAG");
        msgMap.put(LOGINFAIL, "LOGINFAIL");
        msgMap.put(LOGIN2, "LOGIN2");
        msgMap.put(KIGUI, "KIGUI");
        msgMap.put(ENEMY_LIST, "ENEMY_LIST");
        msgMap.put(ANDROID_IAP, "ANDROID_IAP");
        msgMap.put(UPDATE_ACTIVEPOINT, "UPDATE_ACTIVEPOINT");
        msgMap.put(TOP, "TOP");
        msgMap.put(MOB_ME_UPDATE, "MOB_ME_UPDATE");
        msgMap.put(UPDATE_COOLDOWN, "UPDATE_COOLDOWN");
        msgMap.put(BGITEM_VERSION, "BGITEM_VERSION");
        msgMap.put(SET_CLIENTTYPE, "SET_CLIENTTYPE");
        msgMap.put(MAP_TRASPORT, "MAP_TRASPORT");
        msgMap.put(UPDATE_BODY, "UPDATE_BODY");
        msgMap.put(SERVERSCREEN, "SERVERSCREEN");
        msgMap.put(UPDATE_DATA, "UPDATE_DATA");
        msgMap.put(GIAO_DICH, "GIAO_DICH");
        msgMap.put(MOB_CAPCHA, "MOB_CAPCHA");
        msgMap.put(MOB_MAX_HP, "MOB_MAX_HP");
        msgMap.put(CALL_DRAGON, "CALL_DRAGON");
        msgMap.put(TILE_SET, "TILE_SET");
        msgMap.put(COMBINNE, "COMBINNE");
        msgMap.put(FRIEND, "FRIEND");
        msgMap.put(PLAYER_MENU, "PLAYER_MENU");
        msgMap.put(CHECK_MOVE, "CHECK_MOVE");
        msgMap.put(SMALLIMAGE_VERSION, "SMALLIMAGE_VERSION");
        msgMap.put(ARCHIVEMENT, "ARCHIVEMENT");
        msgMap.put(NPC_BOSS, "NPC_BOSS");
        msgMap.put(GET_IMAGE_SOURCE, "GET_IMAGE_SOURCE");
        msgMap.put(NPC_ADD_REMOVE, "NPC_ADD_REMOVE");
        msgMap.put(CHAT_PLAYER, "CHAT_PLAYER");
        msgMap.put(CHAT_THEGIOI_CLIENT, "CHAT_THEGIOI_CLIENT");
        msgMap.put(BIG_MESSAGE, "BIG_MESSAGE");
        msgMap.put(MAXSTAMINA, "MAXSTAMINA");
        msgMap.put(STAMINA, "STAMINA");
        msgMap.put(REQUEST_ICON, "REQUEST_ICON");
        msgMap.put(GET_EFFDATA, "GET_EFFDATA");
        msgMap.put(TELEPORT, "TELEPORT");
        msgMap.put(UPDATE_BAG, "UPDATE_BAG");
        msgMap.put(GET_BAG, "GET_BAG");
        msgMap.put(CLAN_IMAGE, "CLAN_IMAGE");
        msgMap.put(UPDATE_CLANID, "UPDATE_CLANID");
        msgMap.put(SKILL_NOT_FOCUS, "SKILL_NOT_FOCUS");
        msgMap.put(SHOP, "SHOP");
        msgMap.put(USE_ITEM, "USE_ITEM");
        msgMap.put(ME_LOAD_POINT, "ME_LOAD_POINT");
        msgMap.put(UPDATE_CAPTION, "UPDATE_CAPTION");
        msgMap.put(GET_ITEM, "GET_ITEM");
        msgMap.put(FINISH_LOADMAP, "FINISH_LOADMAP");
        msgMap.put(FINISH_UPDATE, "FINISH_UPDATE");
        msgMap.put(BODY, "BODY");
        msgMap.put(BAG, "BAG");
        msgMap.put(BOX, "BOX");
        msgMap.put(MAGIC_TREE, "MAGIC_TREE");
        msgMap.put(MAP_OFFLINE, "MAP_OFFLINE");
        msgMap.put(BACKGROUND_TEMPLATE, "BACKGROUND_TEMPLATE");
        msgMap.put(ITEM_BACKGROUND, "ITEM_BACKGROUND");
        msgMap.put(SUB_COMMAND, "SUB_COMMAND");
        msgMap.put(NOT_LOGIN, "NOT_LOGIN");
        msgMap.put(NOT_MAP, "NOT_MAP");
        msgMap.put(GET_SESSION_ID, "GET_SESSION_ID");
        msgMap.put(DIALOG_MESSAGE, "DIALOG_MESSAGE");
        msgMap.put(SERVER_MESSAGE, "SERVER_MESSAGE");
        msgMap.put(MAP_INFO, "MAP_INFO");
        msgMap.put(MAP_CHANGE, "MAP_CHANGE");
        msgMap.put(MAP_CLEAR, "MAP_CLEAR");
        msgMap.put(ITEMMAP_REMOVE, "ITEMMAP_REMOVE");
        msgMap.put(ITEMMAP_MYPICK, "ITEMMAP_MYPICK");
        msgMap.put(ITEMMAP_PLAYERPICK, "ITEMMAP_PLAYERPICK");
        msgMap.put(ME_THROW, "ME_THROW");
        msgMap.put(ME_DIE, "ME_DIE");
        msgMap.put(ME_LIVE, "ME_LIVE");
        msgMap.put(ME_BACK, "ME_BACK");
        msgMap.put(PLAYER_THROW, "PLAYER_THROW");
        msgMap.put(NPC_LIVE, "NPC_LIVE");
        msgMap.put(NPC_DIE, "NPC_DIE");
        msgMap.put(NPC_ATTACK_ME, "NPC_ATTACK_ME");
        msgMap.put(NPC_ATTACK_PLAYER, "NPC_ATTACK_PLAYER");
        msgMap.put(MOB_HP, "MOB_HP");
        msgMap.put(PLAYER_DIE, "PLAYER_DIE");
        msgMap.put(PLAYER_MOVE, "PLAYER_MOVE");
        msgMap.put(PLAYER_REMOVE, "PLAYER_REMOVE");
        msgMap.put(PLAYER_ADD, "PLAYER_ADD");
        msgMap.put(PLAYER_ATTACK_N_P, "PLAYER_ATTACK_N_P");
        msgMap.put(PLAYER_UP_EXP, "PLAYER_UP_EXP");
        msgMap.put(ME_UP_COIN_LOCK, "ME_UP_COIN_LOCK");
        msgMap.put(ME_CHANGE_COIN, "ME_CHANGE_COIN");
        msgMap.put(ITEM_BUY, "ITEM_BUY");
        msgMap.put(ITEM_SALE, "ITEM_SALE");
        msgMap.put(UPPEARL_LOCK, "UPPEARL_LOCK");
        msgMap.put(UPGRADE, "UPGRADE");
        msgMap.put(PLEASE_INPUT_PARTY, "PLEASE_INPUT_PARTY");
        msgMap.put(ACCEPT_PLEASE_PARTY, "ACCEPT_PLEASE_PARTY");
        msgMap.put(REQUEST_PLAYERS, "REQUEST_PLAYERS");
        msgMap.put(UPDATE_ACHIEVEMENT, "UPDATE_ACHIEVEMENT");
        msgMap.put(PHUBANG_INFO, "PHUBANG_INFO");
        msgMap.put(ZONE_CHANGE, "ZONE_CHANGE");
        msgMap.put(MENU, "MENU");
        msgMap.put(OPEN_UI, "OPEN_UI");
        msgMap.put(OPEN_UI_PT, "OPEN_UI_PT");
        msgMap.put(OPEN_UI_SHOP, "OPEN_UI_SHOP");
        msgMap.put(OPEN_MENU_ID, "OPEN_MENU_ID");
        msgMap.put(OPEN_UI_COLLECT, "OPEN_UI_COLLECT");
        msgMap.put(OPEN_UI_ZONE, "OPEN_UI_ZONE");
        msgMap.put(OPEN_UI_TRADE, "OPEN_UI_TRADE");
        msgMap.put(OPEN_UI_SAY, "OPEN_UI_SAY");
        msgMap.put(OPEN_UI_CONFIRM, "OPEN_UI_CONFIRM");
        msgMap.put(OPEN_UI_MENU, "OPEN_UI_MENU");
        msgMap.put(SKILL_SELECT, "SKILL_SELECT");
        msgMap.put(REQUEST_ITEM_INFO, "REQUEST_ITEM_INFO");
        msgMap.put(TRADE_INVITE, "TRADE_INVITE");
        msgMap.put(TRADE_INVITE_ACCEPT, "TRADE_INVITE_ACCEPT");
        msgMap.put(TRADE_LOCK_ITEM, "TRADE_LOCK_ITEM");
        msgMap.put(TRADE_ACCEPT, "TRADE_ACCEPT");
        msgMap.put(TASK_GET, "TASK_GET");
        msgMap.put(TASK_NEXT, "TASK_NEXT");
        msgMap.put(GAME_INFO, "GAME_INFO");
        msgMap.put(TASK_UPDATE, "TASK_UPDATE");
        msgMap.put(CHAT_MAP, "CHAT_MAP");
        msgMap.put(NPC_MISS, "NPC_MISS");
        msgMap.put(RESET_POINT, "RESET_POINT");
        msgMap.put(ALERT_MESSAGE, "ALERT_MESSAGE");
        msgMap.put(AUTO_SERVER, "AUTO_SERVER");
        msgMap.put(ALERT_SEND_SMS, "ALERT_SEND_SMS");
        msgMap.put(TRADE_INVITE_CANCEL, "TRADE_INVITE_CANCEL");
        msgMap.put(BOSS_SKILL, "BOSS_SKILL");
        msgMap.put(MABU_HOLD, "MABU_HOLD");
        msgMap.put(FRIEND_INVITE, "FRIEND_INVITE");
        msgMap.put(PLAYER_ATTACK_NPC, "PLAYER_ATTACK_NPC");
        msgMap.put(HAVE_ATTACK_PLAYER, "HAVE_ATTACK_PLAYER");
        msgMap.put(OPEN_UI_NEWMENU, "OPEN_UI_NEWMENU");
        msgMap.put(MOVE_FAST, "MOVE_FAST");
        msgMap.put(TEST_INVITE, "TEST_INVITE");
        msgMap.put(ADD_CUU_SAT, "ADD_CUU_SAT");
        msgMap.put(ME_CUU_SAT, "ME_CUU_SAT");
        msgMap.put(CLEAR_CUU_SAT, "CLEAR_CUU_SAT");
        msgMap.put(PLAYER_UP_EXPDOWN, "PLAYER_UP_EXPDOWN");
        msgMap.put(ME_DIE_EXP_DOWN, "ME_DIE_EXP_DOWN");
        msgMap.put(PLAYER_ATTACK_P_N, "PLAYER_ATTACK_P_N");
        msgMap.put(ITEMMAP_ADD, "ITEMMAP_ADD");
        msgMap.put(DEL_ACC, "DEL_ACC");
        msgMap.put(USE_SKILL_MY_BUFF, "USE_SKILL_MY_BUFF");
        msgMap.put(NPC_CHANGE, "NPC_CHANGE");
        msgMap.put(PARTY_INVITE, "PARTY_INVITE");
        msgMap.put(PARTY_ACCEPT, "PARTY_ACCEPT");
        msgMap.put(PARTY_CANCEL, "PARTY_CANCEL");
        msgMap.put(PLAYER_IN_PARTY, "PLAYER_IN_PARTY");
        msgMap.put(PARTY_OUT, "PARTY_OUT");
        msgMap.put(FRIEND_ADD, "FRIEND_ADD");
        msgMap.put(NPC_IS_DISABLE, "NPC_IS_DISABLE");
        msgMap.put(NPC_IS_MOVE, "NPC_IS_MOVE");
        msgMap.put(SUMON_ATTACK, "SUMON_ATTACK");
        msgMap.put(RETURN_POINT_MAP, "RETURN_POINT_MAP");
        msgMap.put(NPC_IS_FIRE, "NPC_IS_FIRE");
        msgMap.put(NPC_IS_ICE, "NPC_IS_ICE");
        msgMap.put(NPC_IS_WIND, "NPC_IS_WIND");
        msgMap.put(OPEN_TEXT_BOX_ID, "OPEN_TEXT_BOX_ID");
        msgMap.put(REQUEST_ITEM_PLAYER, "REQUEST_ITEM_PLAYER");
        msgMap.put(CHAT_PRIVATE, "CHAT_PRIVATE");
        msgMap.put(CHAT_THEGIOI_SERVER, "CHAT_THEGIOI_SERVER");
        msgMap.put(CHAT_VIP, "CHAT_VIP");
        msgMap.put(SERVER_ALERT, "SERVER_ALERT");
        msgMap.put(ME_UP_COIN_BAG, "ME_UP_COIN_BAG");
        msgMap.put(GET_TASK_ORDER, "GET_TASK_ORDER");
        msgMap.put(GET_TASK_UPDATE, "GET_TASK_UPDATE");
        msgMap.put(CLEAR_TASK_ORDER, "CLEAR_TASK_ORDER");
        msgMap.put(ADD_ITEM_MAP, "ADD_ITEM_MAP");
        msgMap.put(TRANSPORT, "TRANSPORT");
        msgMap.put(ITEM_TIME, "ITEM_TIME");
        msgMap.put(PET_INFO, "PET_INFO");
        msgMap.put(PET_STATUS, "PET_STATUS");
        msgMap.put(SERVER_DATA, "SERVER_DATA");
        msgMap.put(CLIENT_INPUT, "CLIENT_INPUT");
        msgMap.put(HOLD, "HOLD");
        msgMap.put(SHOW_ADS, "SHOW_ADS");
        msgMap.put(LOGIN_DE, "LOGIN_DE");
        msgMap.put(SET_POS, "SET_POS");
        msgMap.put(NPC_CHAT, "NPC_CHAT");
        msgMap.put(FUSION, "FUSION");
        msgMap.put(ANDROID_PACK, "ANDROID_PACK");
        msgMap.put(GET_IMAGE_SOURCE2, "GET_IMAGE_SOURCE2");
        msgMap.put(CHAGE_MOD_BODY, "CHAGE_MOD_BODY");
        msgMap.put(CHANGE_ONSKILL, "CHANGE_ONSKILL");
        msgMap.put(REQUEST_PEAN, "REQUEST_PEAN");
        msgMap.put(POWER_INFO, "POWER_INFO");
        msgMap.put(AUTOPLAY, "AUTOPLAY");
        msgMap.put(MABU, "MABU");
        msgMap.put(THACHDAU, "THACHDAU");
        msgMap.put(THELUC, "THELUC");
        msgMap.put(UPDATECHAR_MP, "UPDATECHAR_MP");
        msgMap.put(REFRESH_ITEM, "REFRESH_ITEM");
        msgMap.put(CHECK_CONTROLLER, "CHECK_CONTROLLER");
        msgMap.put(CHECK_MAP, "CHECK_MAP");
        msgMap.put(BIG_BOSS, "BIG_BOSS");
        msgMap.put(BIG_BOSS_2, "BIG_BOSS_2");
        msgMap.put(DUAHAU, "DUAHAU");
        msgMap.put(QUAYSO, "QUAYSO");
        msgMap.put(USER_INFO, "USER_INFO");
        msgMap.put(OPEN3HOUR, "OPEN3HOUR");
        msgMap.put(STATUS_PET, "STATUS_PET");
        msgMap.put(SPEACIAL_SKILL, "SPEACIAL_SKILL");
        msgMap.put(SERVER_EFFECT, "SERVER_EFFECT");
        msgMap.put(INAPP, "INAPP");
        msgMap.put(LUCKY_ROUND, "LUCKY_ROUND");
        msgMap.put(RADA_CARD, "RADA_CARD");
        msgMap.put(CHAR_EFFECT, "CHAR_EFFECT");
    }

    public static final byte CMD_EXTRA_BIG = 12;

    public static final byte CMD_EXTRA = 24;

    public static final byte EXTRA_LINK = Byte.MAX_VALUE;

    public static final byte LOGIN = 0;

    public static final byte REGISTER = 1;

    public static final byte CLIENT_INFO = 2;

    public static final byte SEND_SMS = 3;

    public static final byte REGISTER_IMAGE = 4;

    public static final byte MESSAGE_TIME = 65;

    public static final byte LOGOUT = 0;

    public static final byte SELECT_PLAYER = 1;

    public static final byte CREATE_PLAYER = 2;

    public static final byte DELETE_PLAYER = 3;

    public static final byte UPDATE_VERSION = 4;

    public static final byte UPDATE_MAP = 6;

    public static final byte UPDATE_SKILL = 7;

    public static final byte UPDATE_ITEM = 8;

    public static final byte REQUEST_SKILL = 9;

    public static final byte REQUEST_MAPTEMPLATE = 10;

    public static final byte REQUEST_MOB_TEMPLATE = 11;

    public static final byte UPDATE_TYPE_PK = 35;

    public static final byte PLAYER_ATTACK_PLAYER = -60;

    public static final byte PLAYER_VS_PLAYER = -59;

    public static final byte CLAN_PARTY = -58;

    public static final byte CLAN_INVITE = -57;

    public static final byte CLAN_REMOTE = -56;

    public static final byte CLAN_LEAVE = -55;

    public static final byte CLAN_DONATE = -54;

    public static final byte CLAN_MESSAGE = -51;

    public static final byte CLAN_UPDATE = -52;

    public static final byte CLAN_INFO = -53;

    public static final byte CLAN_JOIN = -49;

    public static final byte CLAN_MEMBER = -50;

    public static final byte CLAN_SEARCH = -47;

    public static final byte CLAN_CREATE_INFO = -46;

    public static final byte CLIENT_OK = 13;

    public static final byte CLIENT_OK_INMAP = 14;

    public static final byte UPDATE_VERSION_OK = 15;

    public static final byte INPUT_CARD = 16;

    public static final byte CLEAR_TASK = 17;

    public static final byte CHANGE_NAME = 18;

    public static final byte UPDATE_PK = 20;

    public static final byte CREATE_CLAN = 21;

    public static final byte CONVERT_UPGRADE = 33;

    public static final byte INVITE_CLANDUN = 34;

    public static final byte NOT_USEACC = 35;

    public static final byte ME_LOAD_ACTIVE = 36;

    public static final byte ME_ACTIVE = 37;

    public static final byte ME_UPDATE_ACTIVE = 38;

    public static final byte ME_OPEN_LOCK = 39;

    public static final byte ITEM_SPLIT = 40;

    public static final byte ME_CLEAR_LOCK = 41;

    public static final byte GET_IMG_BY_NAME = 66;

    public static final byte ME_LOAD_ALL = 0;

    public static final byte ME_LOAD_CLASS = 1;

    public static final byte ME_LOAD_SKILL = 2;

    public static final byte ME_LOAD_INFO = 4;

    public static final byte ME_LOAD_HP = 5;

    public static final byte ME_LOAD_MP = 6;

    public static final byte PLAYER_LOAD_ALL = 7;

    public static final byte PLAYER_SPEED = 8;

    public static final byte PLAYER_LOAD_LEVEL = 9;

    public static final byte PLAYER_LOAD_VUKHI = 10;

    public static final byte PLAYER_LOAD_AO = 11;

    public static final byte PLAYER_LOAD_QUAN = 12;

    public static final byte PLAYER_LOAD_BODY = 13;

    public static final byte PLAYER_LOAD_HP = 14;

    public static final byte PLAYER_LOAD_LIVE = 15;

    public static final byte GOTO_PLAYER = 18;

    public static final byte POTENTIAL_UP = 16;

    public static final byte SKILL_UP = 17;

    public static final byte BAG_SORT = 18;

    public static final byte BOX_SORT = 19;

    public static final byte BOX_COIN_OUT = 21;

    public static final byte REQUEST_ITEM = 22;

    public static final byte ME_ADD_SKILL = 23;

    public static final byte ME_UPDATE_SKILL = 62;

    public static final byte GET_PLAYER_MENU = 63;

    public static final byte PLAYER_MENU_ACTION = 64;

    public static final byte SAVE_RMS = 60;

    public static final byte LOAD_RMS = 61;

    public static final byte USE_BOOK_SKILL = 43;

    public static final byte LOCK_INVENTORY = -104;

    public static final byte CHANGE_FLAG = -103;

    public static final byte LOGINFAIL = -102;

    public static final byte LOGIN2 = -101;

    public static final byte KIGUI = -100;

    public static final byte ENEMY_LIST = -99;

    public static final byte ANDROID_IAP = -98;

    public static final byte UPDATE_ACTIVEPOINT = -97;

    public static final byte TOP = -96;

    public static final byte MOB_ME_UPDATE = -95;

    public static final byte UPDATE_COOLDOWN = -94;

    public static final byte BGITEM_VERSION = -93;

    public static final byte SET_CLIENTTYPE = -92;

    public static final byte MAP_TRASPORT = -91;

    public static final byte UPDATE_BODY = -90;

    public static final byte SERVERSCREEN = -88;

    public static final byte UPDATE_DATA = -87;

    public static final byte GIAO_DICH = -86;

    public static final byte MOB_CAPCHA = -85;

    public static final byte MOB_MAX_HP = -84;

    public static final byte CALL_DRAGON = -83;

    public static final byte TILE_SET = -82;

    public static final byte COMBINNE = -81;

    public static final byte FRIEND = -80;

    public static final byte PLAYER_MENU = -79;

    public static final byte CHECK_MOVE = -78;

    public static final byte SMALLIMAGE_VERSION = -77;

    public static final byte ARCHIVEMENT = -76;

    public static final byte NPC_BOSS = -75;

    public static final byte GET_IMAGE_SOURCE = -74;

    public static final byte NPC_ADD_REMOVE = -73;

    public static final byte CHAT_PLAYER = -72;

    public static final byte CHAT_THEGIOI_CLIENT = -71;

    public static final byte BIG_MESSAGE = -70;

    public static final byte MAXSTAMINA = -69;

    public static final byte STAMINA = -68;

    public static final byte REQUEST_ICON = -67;

    public static final byte GET_EFFDATA = -66;

    public static final byte TELEPORT = -65;

    public static final byte UPDATE_BAG = -64;

    public static final byte GET_BAG = -63;

    public static final byte CLAN_IMAGE = -62;

    public static final byte UPDATE_CLANID = -61;

    public static final byte SKILL_NOT_FOCUS = -45;

    public static final byte SHOP = -44;

    public static final byte USE_ITEM = -43;

    public static final byte ME_LOAD_POINT = -42;

    public static final byte UPDATE_CAPTION = -41;

    public static final byte GET_ITEM = -40;

    public static final byte FINISH_LOADMAP = -39;

    public static final byte FINISH_UPDATE = -38;

    public static final byte BODY = -37;

    public static final byte BAG = -36;

    public static final byte BOX = -35;

    public static final byte MAGIC_TREE = -34;

    public static final byte MAP_OFFLINE = -33;

    public static final byte BACKGROUND_TEMPLATE = -32;

    public static final byte ITEM_BACKGROUND = -31;

    public static final byte SUB_COMMAND = -30;

    public static final byte NOT_LOGIN = -29;

    public static final byte NOT_MAP = -28;

    public static final byte GET_SESSION_ID = -27;

    public static final byte DIALOG_MESSAGE = -26;

    public static final byte SERVER_MESSAGE = -25;

    public static final byte MAP_INFO = -24;

    public static final byte MAP_CHANGE = -23;

    public static final byte MAP_CLEAR = -22;

    public static final byte ITEMMAP_REMOVE = -21;

    public static final byte ITEMMAP_MYPICK = -20;

    public static final byte ITEMMAP_PLAYERPICK = -19;

    public static final byte ME_THROW = -18;

    public static final byte ME_DIE = -17;

    public static final byte ME_LIVE = -16;

    public static final byte ME_BACK = -15;

    public static final byte PLAYER_THROW = -14;

    public static final byte NPC_LIVE = -13;

    public static final byte NPC_DIE = -12;

    public static final byte NPC_ATTACK_ME = -11;

    public static final byte NPC_ATTACK_PLAYER = -10;

    public static final byte MOB_HP = -9;

    public static final byte PLAYER_DIE = -8;

    public static final byte PLAYER_MOVE = -7;

    public static final byte PLAYER_REMOVE = -6;

    public static final byte PLAYER_ADD = -5;

    public static final byte PLAYER_ATTACK_N_P = -4;

    public static final byte PLAYER_UP_EXP = -3;

    public static final byte ME_UP_COIN_LOCK = -2;

    public static final byte ME_CHANGE_COIN = -1;

    public static final byte ITEM_BUY = 6;

    public static final byte ITEM_SALE = 7;

    public static final byte UPPEARL_LOCK = 13;

    public static final byte UPGRADE = 14;

    public static final byte PLEASE_INPUT_PARTY = 16;

    public static final byte ACCEPT_PLEASE_PARTY = 17;

    public static final byte REQUEST_PLAYERS = 18;

    public static final byte UPDATE_ACHIEVEMENT = 19;

    public static final byte PHUBANG_INFO = 20;

    public static final byte ZONE_CHANGE = 21;

    public static final byte MENU = 22;

    public static final byte OPEN_UI = 23;

    public static final byte OPEN_UI_PT = 25;

    public static final byte OPEN_UI_SHOP = 26;

    public static final byte OPEN_MENU_ID = 27;

    public static final byte OPEN_UI_COLLECT = 28;

    public static final byte OPEN_UI_ZONE = 29;

    public static final byte OPEN_UI_TRADE = 30;

    public static final byte OPEN_UI_SAY = 38;

    public static final byte OPEN_UI_CONFIRM = 32;

    public static final byte OPEN_UI_MENU = 33;

    public static final byte SKILL_SELECT = 34;

    public static final byte REQUEST_ITEM_INFO = 35;

    public static final byte TRADE_INVITE = 36;

    public static final byte TRADE_INVITE_ACCEPT = 37;

    public static final byte TRADE_LOCK_ITEM = 38;

    public static final byte TRADE_ACCEPT = 39;

    public static final byte TASK_GET = 40;

    public static final byte TASK_NEXT = 41;

    public static final byte GAME_INFO = 50;

    public static final byte TASK_UPDATE = 43;

    public static final byte CHAT_MAP = 44;

    public static final byte NPC_MISS = 45;

    public static final byte RESET_POINT = 46;

    public static final byte ALERT_MESSAGE = 47;

    public static final byte AUTO_SERVER = 48;

    public static final byte ALERT_SEND_SMS = 49;

    public static final byte TRADE_INVITE_CANCEL = 50;

    public static final byte BOSS_SKILL = 51;

    public static final byte MABU_HOLD = 52;

    public static final byte FRIEND_INVITE = 53;

    public static final byte PLAYER_ATTACK_NPC = 54;

    public static final byte HAVE_ATTACK_PLAYER = 56;

    public static final byte OPEN_UI_NEWMENU = 57;

    public static final byte MOVE_FAST = 58;

    public static final byte TEST_INVITE = 59;

    public static final byte ADD_CUU_SAT = 62;

    public static final byte ME_CUU_SAT = 63;

    public static final byte CLEAR_CUU_SAT = 64;

    public static final byte PLAYER_UP_EXPDOWN = 65;

    public static final byte ME_DIE_EXP_DOWN = 66;

    public static final byte PLAYER_ATTACK_P_N = 67;

    public static final byte ITEMMAP_ADD = 68;

    public static final byte DEL_ACC = 69;

    public static final byte USE_SKILL_MY_BUFF = 70;

    public static final byte NPC_CHANGE = 74;

    public static final byte PARTY_INVITE = 75;

    public static final byte PARTY_ACCEPT = 76;

    public static final byte PARTY_CANCEL = 77;

    public static final byte PLAYER_IN_PARTY = 78;

    public static final byte PARTY_OUT = 79;

    public static final byte FRIEND_ADD = 80;

    public static final byte NPC_IS_DISABLE = 81;

    public static final byte NPC_IS_MOVE = 82;

    public static final byte SUMON_ATTACK = 83;

    public static final byte RETURN_POINT_MAP = 84;

    public static final byte NPC_IS_FIRE = 85;

    public static final byte NPC_IS_ICE = 86;

    public static final byte NPC_IS_WIND = 87;

    public static final byte OPEN_TEXT_BOX_ID = 88;

    public static final byte REQUEST_ITEM_PLAYER = 90;

    public static final byte CHAT_PRIVATE = 91;

    public static final byte CHAT_THEGIOI_SERVER = 92;

    public static final byte CHAT_VIP = 93;

    public static final byte SERVER_ALERT = 94;

    public static final byte ME_UP_COIN_BAG = 95;

    public static final byte GET_TASK_ORDER = 96;

    public static final byte GET_TASK_UPDATE = 97;

    public static final byte CLEAR_TASK_ORDER = 98;

    public static final byte ADD_ITEM_MAP = 99;

    public static final byte TRANSPORT = -105;

    public static final byte ITEM_TIME = -106;

    public static final byte PET_INFO = -107;

    public static final byte PET_STATUS = -108;

    public static final byte SERVER_DATA = -110;

    public static final byte CLIENT_INPUT = -125;

    public static final byte HOLD = -124;

    public static final byte SHOW_ADS = 121;

    public static final byte LOGIN_DE = 122;

    public static final byte SET_POS = 123;

    public static final byte NPC_CHAT = 124;

    public static final byte FUSION = 125;

    public static final byte ANDROID_PACK = 126;

    public static final byte GET_IMAGE_SOURCE2 = -111;

    public static final byte CHAGE_MOD_BODY = -112;

    public static final byte CHANGE_ONSKILL = -113;

    public static final byte REQUEST_PEAN = -114;

    public static final byte POWER_INFO = -115;

    public static final byte AUTOPLAY = -116;

    public static final byte MABU = -117;

    public static final byte THACHDAU = -118;

    public static final byte THELUC = -119;

    public static final byte UPDATECHAR_MP = -123;

    public static final byte REFRESH_ITEM = 100;

    public static final byte CHECK_CONTROLLER = -120;

    public static final byte CHECK_MAP = -121;

    public static final byte BIG_BOSS = 101;

    public static final byte BIG_BOSS_2 = 102;

    public static final byte DUAHAU = -122;

    public static final byte QUAYSO = -126;

    public static final byte USER_INFO = 42;

    public static final byte OPEN3HOUR = -89;

    public static final byte STATUS_PET = 31;

    public static final byte SPEACIAL_SKILL = 112;

    public static final byte SERVER_EFFECT = 113;

    public static final byte INAPP = 114;

    public static final byte LUCKY_ROUND = -127;

    public static final byte RADA_CARD = Byte.MAX_VALUE;

    public static final byte CHAR_EFFECT = Byte.MIN_VALUE;
}
