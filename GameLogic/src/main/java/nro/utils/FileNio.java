package nro.utils;

import nro.server.LogServer;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FileNio {

    // Bộ nhớ cache lưu trữ dữ liệu file để tránh đọc lại từ đĩa cho cùng một URL
    public static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();

    public static byte[] loadDataFile(String url) {
        try {
            byte[] ab = CACHE.get(url);
            if (ab == null) {
                try (FileChannel fileChannel = FileChannel.open(Paths.get(url))) {
                    // create a byte buffer with the size of the file
                    ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());
                    // doc du lieu tu file channel vao buffer
                    fileChannel.read(buffer);
                    //change type byte buffer chuyen sang read mode
                    buffer.flip();
                    // tao mang byte de chua du lieu tu buffer
                    ab = new byte[buffer.remaining()];
                    // copy du lieu tu buffer vao mang byte
                    buffer.get(ab);
                    // luu tru du lieu vao cache de lan sau khong can doc lai tu file
                    CACHE.put(url, ab);
                }
            }
            return ab;
        } catch (Exception e) {
            LogServer.LogException("Error occurred while reading file at URL: " + url + " - " + e.getMessage());
        }
        return null;
    }

    public static String cutPng(String str) {
        String result = str;
        if (str.contains(".png")) {
            result = str.replace(".png", "");
        }
        return result;
    }

    public static void addPath(ArrayList<File> list, File file) {
        if (file.isFile()) {
            list.add(file);
        } else {
            for (File f : Objects.requireNonNull(file.listFiles())) {
                addPath(list, f);
            }
        }
    }
}
