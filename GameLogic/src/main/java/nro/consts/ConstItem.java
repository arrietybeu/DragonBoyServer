package nro.consts;

public class ConstItem {

    // creator by id

    public static final int SERVER = -300;
    public static final int FLAG_BAG = -299;

    // Item status

    public static final byte TYPE_AO = 0;

    public static final byte TYPE_QUAN = 1;

    public static final byte TYPE_GANG = 2;

    public static final byte TYPE_GIAY = 3;

    public static final byte TYPE_RADA_OR_NHAN = 4;

    public static final byte TYPE_CAI_TRANG_OR_AVATAR = 5;

    public static final byte TYPE_PEA = 6;

    public static final byte TYPE_LEARN_SKILL = 7;

    public static final byte TYPE_GOLD = 9;

    public static final byte TYPE_GEM = 10;

    public static final byte TYPE_FLAG_BAG = 11;

    public static final byte TYPE_DRAGON_BALL = 12;

    public static final byte TYPE_VE_TINH = 22;

    public static final byte TYPE_MOUNT = 23;

    public static final byte TYPE_MOUNT_VIP = 24;

    public static final byte TYPE_SYNTHESIS = 27;

    public static final byte TYPE_GIAP_LUYEN_TAP = 32;

    public static final byte TYPE_RUBY = 34;

    public static final byte TYPE_SACH_TUYET_KY = 35;

    // Item ID

    public static final int NGOC_RONG_1_SAO = 14;

    public static final int NGOC_RONG_2_SAO = 15;

    public static final int NGOC_RONG_3_SAO = 16;

    public static final int NGOC_RONG_4_SAO = 17;

    public static final int NGOC_RONG_5_SAO = 18;

    public static final int NGOC_RONG_6_SAO = 19;

    public static final int NGOC_RONG_7_SAO = 20;

    public static final short GANG_VAI_DEN = 21;

    public static final short GANG_SOI_LEN = 22;

    public static final short GANG_VAI_THO = 23;

    public static final short DUI_GA = 73;

    public static final short DUI_GA_NUONG = 74;

    public static final short DUI_HEO_XAYDA = 75;

    public static final short TRUYEN_TRANH = 85;

    public static final short DUA_BE = 78;

    public static final int SACH_KAMEJOKO_LV1 = 94;

    public static final int SACH_MASENKO_LV1 = 101;

    public static final int SACH_ANTOMIC_LV1 = 108;

    public static final int THAI_DUONG_HA_SAN_LV1 = 115;

    public static final int SACH_HOC_TRI_THUONG_LV1 = 122;

    public static final int TAI_TAO_NANG_LUONG_LV1 = 129;

    public static final short VANG_1 = 188;

    public static final short VANG_2 = 189;

    public static final short VANG_3 = 190;

    public static final short GOI_10_VIEN_CAPSULE = 193;

    public static final short VIEN_CAPSULE_DAC_BIET = 194;

    public static final short RUBY = 861;

    public static final short SOCOLA = 516;

    public static final short TU_DONG_LUYEN_TAP_CAP_1 = 521;

    public static final short QUA_TRUNG = 568;


    public static int getIdItemTaskEleven(int gender) {
        return switch (gender) {
            case ConstPlayer.NAMEC -> ConstItem.SACH_MASENKO_LV1;
            case ConstPlayer.XAYDA -> ConstItem.SACH_ANTOMIC_LV1;
            default -> ConstItem.SACH_KAMEJOKO_LV1;
        };
    }

    public static String getIdItemTaskEleven2(int gender) {
        return switch (gender) {
            case ConstPlayer.NAMEC -> "Sách học Trị thương lv1";
            case ConstPlayer.XAYDA -> "Tái tạo năng lượng lv1";
            default -> "Thái Dương Hạ San lv1";
        };
    }

}
