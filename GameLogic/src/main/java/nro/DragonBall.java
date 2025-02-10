/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro;

import org.jsoup.Jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Arriety
 */
public class DragonBall {

    public static ImageInfo[] imgInfo;

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

    }

    public static void main(String args) {
//        loadFileEff();
//        loadImageVersion();
        for (int i = 1; i < 4; i++) {
            String directoryPath = "C:\\Users\\THANH\\Desktop\\ClientDragon\\ExportedProject\\Assets\\Resources\\res\\x" + i + "\\mainimage";
            deleteAssetFiles(directoryPath);
        }

//        String directoryPath = "C:\\Users\\YourUsername\\Documents"; // Thay đổi đường dẫn tại đây

        /**
         * x1 || 101
         * x2 || 1055
         * x3 || 1056
         * x4 || 1052
         */

//        int fileCount = countFilesInDirectory(directoryPath);
//        System.out.println("Số lượng file trong thư mục \"" + directoryPath + "\": " + fileCount);

        System.out.println("hi: " + 0x7fffffff);
    }

    public static int countFilesInDirectory(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Đường dẫn không hợp lệ hoặc không phải là thư mục.");
            return 0;
        }
        File[] files = directory.listFiles();
        // Kiểm tra nếu thư mục rỗng
        if (files == null || files.length == 0) {
            System.out.println("Thư mục không chứa tệp nào.");
            return 0;
        }

        // Đếm số lượng tệp
        int fileCount = 0;
        for (File file : files) {
            if (file.isFile()) {
                fileCount++;
            }
        }

        return fileCount;
    }

    public static void deleteAssetFiles(String directoryPath) {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Đường dẫn không hợp lệ hoặc không phải thư mục: " + directoryPath);
            return;
        }

        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".asset")) {
                    if (file.delete()) {
                        System.out.println("Đã xóa tệp: " + file.getAbsolutePath());
                    } else {
                        System.out.println("Không thể xóa tệp: " + file.getAbsolutePath());
                    }
                }
            }
        } else {
            System.out.println("Không có tệp nào trong thư mục.");
        }
    }

    public static void loadImageVersion() {
        try {
            String path = "C:\\Users\\THANH\\Desktop\\Shortcuts_Serverne\\resources\\data\\nro\\data_img_version\\x4\\img_version";
            DataInputStream dis = new DataInputStream(new FileInputStream(path));
            short num10 = dis.readShort();
            for (int l = 0; l < num10; l++) {
                String iD = dis.readUTF();
                byte version = (byte) dis.readUnsignedByte();
                System.out.println("id: " + iD + " version: " + version);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFileEff() {
        try {

            String path = "C:\\Users\\THANH\\Desktop\\Shortcuts_Serverne\\Eff\\effect\\x4\\data\\DataEffect_26";
//            String path = "C:\\Users\\THANH\\Desktop\\GameServer\\GameLogic\\src\\main\\resources\\x2\\effect\\data\\26";

            DataInputStream dis = new DataInputStream(new FileInputStream(path));

            var b = dis.readByte();

            imgInfo = new ImageInfo[b];

            for (int i = 0; i < b; i++) {
                imgInfo[i] = new ImageInfo();
                imgInfo[i].id = dis.readByte();
                imgInfo[i].x = dis.readUnsignedByte();
                imgInfo[i].y = dis.readUnsignedByte();
                imgInfo[i].w = dis.readUnsignedByte();
                imgInfo[i].h = dis.readUnsignedByte();

                System.out.println("id:" + imgInfo[i].id + " x: " + imgInfo[i].x + " y:" + imgInfo[i].y + " w:" + imgInfo[i].w + " h: " + imgInfo[i].h);
            }
            short num5 = dis.readShort();

            for (int j = 0; j < num5; j++) {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ImageInfo {

        public int id;
        public int x;
        public int y;
        public int w;
        public int h;
    }
}
