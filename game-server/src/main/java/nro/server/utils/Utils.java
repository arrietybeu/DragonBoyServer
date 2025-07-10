package nro.server.utils;

import java.util.regex.Pattern;

/**
 * @author Arriety
 */
public class Utils {

    public static final int[][] VALID_HAIR_IDS = {
            {64, 30, 31}, // Tóc cho Trái Đất (gender 0)
            {9, 29, 32},  // Tóc cho Namec (gender 1)
            {6, 27, 28}   // Tóc cho Xayda (gender 2)
    };

    public static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{5,15}$");

}
