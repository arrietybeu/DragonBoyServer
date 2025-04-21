package nro.utils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;
import java.util.List;

public class CaptchaUtil {

    public static final int DIGITS_LENGTH = 5;

    static final int WIDTH = 256;
    static final int HEIGHT = 256;

    public static Map<Character, Character> RULES = new HashMap<>();
    public static Map<Character, Character> REVERSE_MAP = new HashMap<>();

    public static void generateDynamicRules() {
        String possibleChars = "ABCDEFGHJKLMNPQRSTUVWXYZ#%&@x$";
        String digits = "123456789";
        List<Character> charPool = new ArrayList<>();
        List<Character> digitPool = new ArrayList<>();

        for (char c : possibleChars.toCharArray()) charPool.add(c);
        for (char d : digits.toCharArray()) digitPool.add(d);

        Collections.shuffle(charPool);
        Collections.shuffle(digitPool);

        RULES.clear();
        REVERSE_MAP.clear();

        for (int i = 0; i < 6; i++) {
            RULES.put(charPool.get(i), digitPool.get(i));
            REVERSE_MAP.put(digitPool.get(i), charPool.get(i));
        }
    }

    static Color randomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(205) + 50, rand.nextInt(205) + 50, rand.nextInt(205) + 50);
    }

    public static byte[] createCaptchaToBytes(String digits) throws Exception {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        Font fontNoise = new Font("Monospaced", Font.PLAIN, 12);
        Font fontTop = new Font("Arial", Font.BOLD, 32);
        Font fontRule = new Font("Arial", Font.BOLD, 16);

        Random rand = new Random();

        g.setFont(fontNoise);
        for (int i = 0; i < 200; i++) {
            g.setColor(new Color(150, 150, 150, 80));
            char c = (char) (rand.nextInt(26) + 'a');
            int x = rand.nextInt(WIDTH);
            int y = rand.nextInt(HEIGHT);
            g.drawString(String.valueOf(c), x, y);
        }

        List<Character> chars = new ArrayList<>();
        for (char digit : digits.toCharArray()) {
            chars.add(REVERSE_MAP.getOrDefault(digit, '?'));
        }

        int spacing = WIDTH / (chars.size() + 1);
        List<Point> topPositions = new ArrayList<>();
        for (int i = 0; i < chars.size(); i++) {
            char ch = chars.get(i);
            int x = spacing * (i + 1);
            int y = 60;
            g.setColor(randomColor());

            // xoay ký tự
            double angle = Math.toRadians(rand.nextInt(41) - 20); // -20 đến +20 độ
            AffineTransform old = g.getTransform();
            g.rotate(angle, x, y);
            g.setFont(fontTop);
            g.drawString(String.valueOf(ch), x, y);
            g.setTransform(old);

            topPositions.add(new Point(x, y));
        }

        List<Map.Entry<Character, Character>> rulesList = new ArrayList<>(RULES.entrySet());
        Collections.shuffle(rulesList);
        g.setFont(fontRule);
        int ruleSpacing = WIDTH / (rulesList.size() + 1);
        List<Point> rulePositions = new ArrayList<>();
        for (int i = 0; i < rulesList.size(); i++) {
            Map.Entry<Character, Character> entry = rulesList.get(i);
            int x = ruleSpacing * (i + 1);
            int y = HEIGHT - 50;
            String rule = entry.getKey() + "->" + entry.getValue();
            g.setColor(randomColor());
            g.drawString(rule, x, y);
            rulePositions.add(new Point(x, y));
        }

        g.setStroke(new BasicStroke(1));
        for (Point pt : topPositions) {
            g.setColor(randomColor());
            g.drawLine(pt.x - 15, pt.y - 10, pt.x + 15, pt.y - 10);
        }

        for (Point pt : rulePositions) {
            g.setColor(randomColor());
            g.drawLine(pt.x - 15, pt.y - 10, pt.x + 30, pt.y - 10);
        }

        g.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }



}
