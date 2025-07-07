package nro.server.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

/**
 * @author Arriety
 */
public class CaptchaSwingViewer {

    public static void main(String[] args) throws Exception {
        CaptchaUtil.generateDynamicRules();
        String digits = "19062006";

        byte[] imageBytes = CaptchaUtil.createCaptchaToBytes(digits);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

        JFrame frame = new JFrame("test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(CaptchaUtil.WIDTH, CaptchaUtil.HEIGHT);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };

        frame.setContentPane(panel);
        frame.setVisible(true);
    }
}
