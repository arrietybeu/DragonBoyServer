package nro.server.utils;

import java.util.regex.Pattern;

/**
 * @author Arriety
 */
public final class Utils {

    public static final int[][] VALID_HAIR_IDS = {
            {64, 30, 31}, // Tóc cho Trái Đất (gender 0)
            {9, 29, 32},  // Tóc cho Namec (gender 1)
            {6, 27, 28}   // Tóc cho Xayda (gender 2)
    };

    private static final short[][] BIRD_FRAMES = {
            {281, 361, 351},
            {512, 513, 536},
            {514, 515, 537}
    };

    public static short[] getPlayerBirdFrames(int gender) {
        if (gender < 0 || gender >= BIRD_FRAMES.length) {
            gender = 2;
        }
        return BIRD_FRAMES[gender];
    }

    public static String[] getPlayerBirdNames(int gender) {
        if (gender < 0 || gender >= BIRD_NAMES.length) {
            gender = 2;
        }
        return BIRD_NAMES[gender];
    }

    private static final String[][] BIRD_NAMES = {
            {"Puaru"}, {"Piano"}, {"Icarus"}
    };

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{5,15}$");

}
