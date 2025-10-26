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

    public static String humanize(long ms) {
        if (ms <= 0) return "0s";

        long seconds = (ms + 999) / 1000;
        if (seconds < 60) return seconds + " giây";

        long minutes = seconds / 60;
        if (minutes < 60) {
            long sec = seconds % 60;
            return sec == 0 ? (minutes + " phút") : (minutes + " phút " + sec + " giây");
        }

        long hours = minutes / 60;
        if (hours < 24) {
            long min = minutes % 60;
            return min == 0 ? (hours + " giờ") : (hours + " giờ " + min + " phút");
        }

        long days = hours / 24;
        long h = hours % 24;
        return h == 0 ? (days + " ngày") : (days + " ngày " + h + " giờ");
    }

    public static String format(String template, Object... args) {
        if (template == null || args == null || args.length == 0) {
            return template;
        }

        StringBuilder sb = new StringBuilder();
        int argIndex = 0;
        int cursor = 0;

        while (cursor < template.length()) {
            int braceIndex = template.indexOf("{}", cursor);

            if (braceIndex == -1 || argIndex >= args.length) {
                sb.append(template.substring(cursor));
                break;
            }

            sb.append(template, cursor, braceIndex);
            sb.append(args[argIndex] != null ? args[argIndex].toString() : "null");

            cursor = braceIndex + 2;
            argIndex++;
        }

        return sb.toString();
    }
}
