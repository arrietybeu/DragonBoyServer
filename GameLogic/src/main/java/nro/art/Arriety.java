package nro.art;

import nro.model.map.GameMap;
import nro.server.manager.MapManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Arriety {

    public static void main(String[] args) {
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
