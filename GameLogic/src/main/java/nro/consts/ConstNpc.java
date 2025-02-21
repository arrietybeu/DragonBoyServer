package nro.consts;

public class ConstNpc {

    public static final int ONG_GOHAN = 0;
    public static final int ONG_PARAGUS = 1;
    public static final int ONG_MOORI = 2;
    public static final int RUONG_DO = 3;
    public static final int DAU_THAN = 4;
    public static final int CON_MEO = 5;
    public static final int KHU_VUC = 6;
    public static final int BUNMA = 7;
    public static final int DENDE = 8;
    public static final int APPULE = 9;
    public static final int DR_BRIEF = 10;
    public static final int CARGO = 11;
    public static final int CUI = 12;
    public static final int QUY_LAO_KAME = 13;
    public static final int TRUONG_LAO_GURU = 14;
    public static final int VUA_VEGETA = 15;
    public static final int URON = 16;
    public static final int BO_MONG = 17;
    public static final int THAN_MEO_KARIN = 18;
    public static final int THUONG_DE = 19;
    public static final int THAN_VU_TRU = 20;
    public static final int BA_HAT_MIT = 21;
    public static final int TRONG_TAI = 22;
    public static final int GHI_DANH = 23;
    public static final int RONG_THIEN = 24;
    public static final int LINH_CANH = 25;
    public static final int DOC_NHAN = 26;
    public static final int RONG_THIEN_NAMEC = 27;
    public static final int CUA_HANG_KY_GUI = 28;
    public static final int RONG_OMEGA = 29;
    public static final int RONG_2_SAO = 30;
    public static final int RONG_3_SAO = 31;
    public static final int RONG_4_SAO = 32;
    public static final int RONG_5_SAO = 33;
    public static final int RONG_6_SAO = 34;
    public static final int RONG_7_SAO = 35;
    public static final int RONG_1_SAO = 36;
    public static final int CA_LICH = 38;
    public static final int SANTA = 39;
    public static final int MABU_MAP = 40;
    public static final int TRUNG_THU = 41;
    public static final int QUOC_VUONG = 42;
    public static final int TO_SU_KAIO = 43;
    public static final int OSIN = 44;
    public static final int KIBIT = 45;
    public static final int BABIDAY = 46;
    public static final int GIU_MA_DAU_BO = 47;
    public static final int NGO_KHONG = 48;
    public static final int DUONG_TANG = 49;
    public static final int QUA_TRUNG = 50;
    public static final int DUA_HAU = 51;
    public static final int HUNG_VUONG = 52;
    public static final int TAPION = 53;
    public static final int LY_TIEU_NUONG = 54;
    public static final int BILL = 55;
    public static final int WHIS = 56;
    public static final int CHAMPA = 57;
    public static final int VADOS = 58;
    public static final int GOKU_SSJ = 60;
    public static final int POTAGE = 62;
    public static final int JACO = 63;
    public static final int YARIRBOE = 65;
    public static final int NOI_BANH = 66;
    public static final int MR_POPO = 67;
    public static final int PANCHY = 68;
    public static final int THO_DAI_CA = 69;
    public static final int BARDOCK = 70;
    public static final int BERRY = 71;
    public static final int CA_DIC = 72;
    public static final int FIDE = 73;
    public static final int TORI_BOT = 74;
    public static final int KING_FURRY = 75;
    public static final int MAI = 76;
    public static final int FU = 77;
    public static final int TEST16 = 78;
    public static final int BONG_HONG = 80;
    public static final int CHI_CHI = 82;
    public static final int DR_MYUU = 83;

    public static String getNpcName(int id) {
        switch (id) {
            case ONG_GOHAN:
                return "Ông Gôhan";
            case ONG_PARAGUS:
                return "Ông Paragus";
            case ONG_MOORI:
                return "Ông Moori";
            case RUONG_DO:
                return "Rương đồ";
            case DAU_THAN:
                return "Đậu thần";
            case CON_MEO:
                return "Con mèo";
            case KHU_VUC:
                return "Khu vực";
            case BUNMA:
                return "Bunma";
            case DENDE:
                return "Dende";
            case APPULE:
                return "Appule";
            case DR_BRIEF:
                return "Dr. Brief";
            case CARGO:
                return "Cargo";
            case CUI:
                return "Cui";
            case QUY_LAO_KAME:
                return "Quy Lão Kame";
            case TRUONG_LAO_GURU:
                return "Trưởng lão Guru";
            case VUA_VEGETA:
                return "Vua Vegeta";
            case URON:
                return "Uron";
            case BO_MONG:
                return "Bò Mộng";
            case THAN_MEO_KARIN:
                return "Thần mèo Karin";
            case THUONG_DE:
                return "Thượng Đế";
            case THAN_VU_TRU:
                return "Thần Vũ Trụ";
            case BA_HAT_MIT:
                return "Bà Hạt Mít";
            case TRONG_TAI:
                return "Trọng tài";
            case GHI_DANH:
                return "Ghi danh";
            case RONG_THIEN:
                return "Rồng Thiêng";
            case LINH_CANH:
                return "Lính canh";
            case DOC_NHAN:
                return "Độc Nhãn";
            case RONG_THIEN_NAMEC:
                return "Rồng Thiêng Namếc";
            case CUA_HANG_KY_GUI:
                return "Cửa hàng ký gửi";
            case RONG_OMEGA:
                return "Rồng Omega";
            case GOKU_SSJ:
                return "Goku SSJ";
            case CHI_CHI:
                return "Chi Chi";
            case DR_MYUU:
                return "Dr. Myuu";
            default:
                return "Unknown NPC";
        }
    }
}
