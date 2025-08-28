package nro.server.utils;

import java.io.*;

/**
 * @author Arriety
 */

public final class LoadTileMap {

    public static int tmw;
    public static int tmh;
    public static int[] maps;
    public static int[] types;

    public static void main() throws IOException {
        String inputFile = "resources/0";

        String outputFile = "resources/maps.sql";

        int mapID = 0;

        loadFromBinary(inputFile);
        exportToSQL(outputFile, mapID);
    }

    // Load dữ liệu tile từ file nhị phân (ví dụ dạng custom, định dạng: byte tmw, byte tmh, rồi tile[])
    public static void loadFromBinary(String filePath) throws IOException {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            tmw = dis.readUnsignedByte();
            tmh = dis.readUnsignedByte();

            maps = new int[tmw * tmh];
            for (int i = 0; i < maps.length; i++) {
                maps[i] = dis.readUnsignedByte(); // hoặc readUnsignedShort nếu là ushort
            }

            types = new int[maps.length];
        }
    }

    // Xuất dữ liệu ra file .sql theo cấu trúc table: (map_id, width, height, tiles)
    public static void exportToSQL(String filePath, int mapID) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            StringBuilder tilesBuilder = new StringBuilder();
            tilesBuilder.append("[");
            for (int i = 0; i < maps.length; i++) {
                tilesBuilder.append(maps[i]);
                if (i != maps.length - 1) {
                    tilesBuilder.append(", ");
                }
            }
            tilesBuilder.append("]");

            String sql = String.format("INSERT INTO map_data (map_id, width, height, tiles) VALUES (%d, %d, %d, '%s');",
                    mapID, tmw, tmh, tilesBuilder.toString());

            writer.write(sql);
            writer.newLine();
        }
    }
}
