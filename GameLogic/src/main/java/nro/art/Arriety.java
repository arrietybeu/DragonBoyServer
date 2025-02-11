package nro.art;

import java.io.*;


public class Arriety {

    public static void main() {
        var filePath = "C:\\Users\\Win Val\\Desktop\\ProjectServer\\resources\\map\\" + 1;
        try (DataInputStream reader = new DataInputStream(new FileInputStream(filePath))) {
            var tmw = reader.readByte();
            var tmh = reader.readByte();
            int[] maps = new int[tmw * tmh];
            for (int i = 0; i < maps.length; i++) {
                maps[i] = reader.readByte();
                System.out.println("maps[" + i + "]: " + maps[i]);
            }
            System.out.println("tmw: " + tmw + " tmh: " + tmh);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
