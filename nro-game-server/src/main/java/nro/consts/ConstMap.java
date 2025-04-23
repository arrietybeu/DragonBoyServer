package nro.consts;

@SuppressWarnings("ALL")
public class ConstMap {

    // --- TILE SET INFO
    public static final int T_EMPTY = 0;
    public static final int T_CENTER = 1;
    public static final int T_TOP = 2;
    public static final int T_LEFT = 4;
    public static final int T_RIGHT = 8;           // Tile phải?
    public static final int T_TREE = 16;
    public static final int T_WATERFALL = 32;
    public static final int T_WATERFLOW = 64;
    public static final int T_TOPFALL = 128;
    public static final int T_OUTSIDE = 256;
    public static final int T_DOWN1PIXEL = 512;
    public static final int T_BRIDGE = 1024;       // Tile cầu
    public static final int T_UNDERWATER = 2048;
    public static final int T_SOLIDGROUND = 4096;
    public static final int T_BOTTOM = 8192;
    public static final int T_DIE = 16384;
    public static final int T_HEBI = 32768;
    public static final int T_BANG = 65536;
    public static final int T_JUM8 = 131072;
    public static final int T_NT0 = 262144;
    public static final int T_NT1 = 524288;

    // --- Grouped logic (gợi ý)
    public static final int T_GROUND_MASK = T_TOP | T_SOLIDGROUND;
    public static final int T_DEADLY_MASK = T_DIE;
    public static final int T_WATER_MASK = T_WATERFALL | T_WATERFLOW | T_UNDERWATER;

    // Map status

    public static final int MAP_TYPE_NORMAL = 0;
    public static final int MAP_OFFLINE = 1;

    // Map id

    public static final short LANG_ARU = 0;
    public static final short DOI_HOA_CUC = 1;
    public static final short THUNG_LUNG_TRE = 2;
    public static final short RUNG_NAM = 3;
    public static final short RUNG_XUONG = 4;
    public static final short DAO_KAME = 5;

    public static final short LANG_MOORI = 7;
    public static final short DOI_NAM_TIM = 8;

    public static final short THI_TRAN_MOORI = 9;
    public static final short THUNG_LUNG_MAIMA = 11;
    public static final short VUC_MAIMA = 12;

    public static final short DAO_GURU = 13;
    public static final short LANG_KAKAROT = 14;
    public static final short DOI_HOANG = 15;
    public static final short LANG_PLANT = 16;
    public static final short RUNG_NGUYEN_SINH = 17;
    public static final short RUNG_THONG_XAYDA = 18;

    public static final short VACH_NUI_DEN = 20;
    public static final short NHA_GOHAN = 21;
    public static final short NHA_MOORI = 22;
    public static final short NHA_BROLY = 23;

    public static final short TRAM_TAU_VU_TRU_TRAI_DAT = 24;
    public static final short TRAM_TAU_VU_TRU_NAMEC = 25;
    public static final short TRAM_TAU_VU_TRU_XAYDA = 26;


    public static final short VACH_NUI_ARU_BASE = 39;
    public static final short VACH_NUI_MOORI_BASE = 40;
    public static final short VUC_PLANT = 41;

    public static final short VACH_NUI_ARU = 42;
    public static final short VACH_NUI_MOORI = 43;
    public static final short VAC_NUI_KAKAROT = 44;

    public static final short THAP_KARIN = 46;
    public static final short RUNG_KARIN = 47;

}
