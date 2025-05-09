package nro.server.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileNio {

    private static final Logger log = LoggerFactory.getLogger(FileNio.class);

    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> FILE_ACCESS_COUNT = new ConcurrentHashMap<>();
    private static final int CACHE_THRESHOLD = 2;

    public static byte[] loadDataFile(String url) {
        lock.readLock().lock();
        try {
            byte[] data = CACHE.get(url);
            if (data != null) return data;
        } finally {
            lock.readLock().unlock();
        }

        byte[] fileData = readFile(url);
        if (fileData == null) return null;

        int count = FILE_ACCESS_COUNT.merge(url, 1, Integer::sum);
        if (count >= CACHE_THRESHOLD) {
            lock.writeLock().lock();
            try {
//                LogServer.LogWarning("ADD cache: " + url + " const: " + count);
                CACHE.put(url, fileData);
            } finally {
                lock.writeLock().unlock();
            }
        }
        return fileData;
    }

    public static byte[] loadDataFileCache(String url) {
        try {
            byte[] ab = CACHE.get(url);
            if (ab == null) {
                try (FileChannel fileChannel = FileChannel.open(Paths.get(url))) {
                    // create a byte buffer with the size of the file
                    ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());
                    // doc du lieu tu file channel vao buffer
                    fileChannel.read(buffer);
                    //change status byte buffer chuyen sang read mode
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
            log.error("Error occurred while reading file at URL: {} - {}", url, e.getMessage());
        }
        return null;
    }

    private static byte[] readFile(String url) {
        try (FileChannel fileChannel = FileChannel.open(Paths.get(url), StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(buffer);
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            return data;
        } catch (Exception e) {
            log.error("Lỗi đọc file: {} - {}", url, e.getMessage(), e);
        }
        return null;
    }

    public static void clearCache() {
        lock.writeLock().lock();
        try {
            CACHE.clear();
            FILE_ACCESS_COUNT.clear();
        } finally {
            lock.writeLock().unlock();
        }
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
