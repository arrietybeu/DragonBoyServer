package nro.consts;

public class ConstPlayer {

    // =================== Gender ===================//
    public static final byte TRAI_DAT = 0;
    public static final byte NAMEC = 1;
    public static final byte XAYDA = 2;

    // =================== Teleport ===================//

    public static final byte TELEPORT_DEFAULT = 0;


    // =================== UP_POTENTIAL ===================//

    public static final byte UP_POTENTIAL_HP = 0;
    public static final byte UP_POTENTIAL_MP = 1;
    public static final byte UP_POTENTIAL_DAMAGE = 2;
    public static final byte UP_POTENTIAL_DEFENSE = 3;
    public static final byte UP_POTENTIAL_CRITICAL = 4;

    public static long[] potentialUse = new long[]{
            50_000_000L,
            250_000_000L,
            1_250_000_000L,
            5_000_000_000L,
            1_500_000_0000L,
            30_000_000_000L,
            45_000_000_000L,
            60_000_000_000L,
            75_000_000_000L,
            90_000_000_000L,
            110_000_000_000L,
            130_000_000_000L,
            150_000_000_000L,
            170_000_000_000L
    };

    // =================== TYPE_ADD_EXP ===================//

    public static final byte TANG_POWER = 0;
    public static final byte TANG_EXP = 1;
    public static final byte ADD_POWER_AND_EXP = 2;

    /// =================== STATUS ===================///

    // type transport map

    public static final byte TYPE_TRANSPORT_CAPSULE = 0;


}
