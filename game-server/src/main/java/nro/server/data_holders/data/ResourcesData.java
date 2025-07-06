package nro.server.data_holders.data;

import lombok.Getter;
import nro.server.data_holders.IManager;
import nro.server.utils.FileNio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * @author Arriety
 */
@Getter
public final class ResourcesData implements IManager {

    private static final Logger log = LoggerFactory.getLogger(ResourcesData.class);

    private final Map<Byte, byte[]> dataSmallVersion = new HashMap<>();
    private byte[][] backgroundVersion;

    @Override
    public void init() throws Throwable {
        initSmallVersion();
        initBgSmallVersion();
    }

    @Override
    public void reload() throws Throwable {
        clear();
        initSmallVersion();
        log.info("ResourcesData reloaded successfully.");
    }

    @Override
    public void clear() throws Throwable {
        dataSmallVersion.clear();
        log.info("ResourcesData cleared successfully.");
    }

    private void initSmallVersion() {
        this.dataSmallVersion.clear();
        try {
            for (int i = 1; i <= 4; i++) {
                byte[] array = new byte[Short.MAX_VALUE];
                File dir = new File(String.format("resources/x%d/icon", i));
                File[] files = dir.listFiles();
                if (files == null)
                    continue;
                int max = -1;
                for (File file : files) {
                    String name = file.getName();
                    try {
                        if (name.contains("."))
                            name = name.substring(0, name.lastIndexOf("."));

                        int id = Integer.parseInt(name);
                        array[id] = (byte) (file.length() % 127);
                        if (id > max)
                            max = id;

                    } catch (Exception e) {
                        System.err.println("Lỗi khi xử lý file: " + file.getName() + " - " + e.getMessage());
                    }
                }
                if (max >= 0)
                    this.dataSmallVersion.put((byte) i, Arrays.copyOf(array, max + 1));
            }
        } catch (Exception e) {
            log.error("Error initializing small version resources: {}", e.getMessage(), e);
        }
    }

    private void initBgSmallVersion() {
        try {
            this.backgroundVersion = new byte[4][];
            for (int i = 1; i <= 4; i++) {
                String path = "resources/x" + i + "/image_background";
                File file = new File(path);
                File[] files = file.listFiles();

                if (files == null || files.length == 0) {
                    log.warn("No files found in path: {}", path);
                    this.backgroundVersion[i] = new byte[0];
                    continue;
                }

                try {
                    backgroundVersion[i - 1] = processFiles(files, path);
                } catch (Exception e) {
                    log.warn("Error processing files in path: {} - {}", path, e.getMessage());
                    backgroundVersion[i - 1] = new byte[0];
                }
            }
        } catch (Exception e) {
            log.error("Error initializing background small version resources: {}", e.getMessage(), e);
        }
    }

    private byte[] processFiles(File[] files, String path) {
        int max = 0;
        List<Integer> ids = new ArrayList<>(files.length);

        for (File f : files) {
            String name = f.getName();
            try {
                int id = Integer.parseInt(FileNio.cutPng(name));
                ids.add(id);
                if (id > max) {
                    max = id;
                }
            } catch (NumberFormatException e) {
                log.error("Error processing file: {} - {}", f.getName(), e.getMessage());
                ids.add(-1);
            }
        }

        byte[] fileData = new byte[max + 1];

        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            int id = ids.get(i);
            if (id < 0) {
                continue;
            }
            try {
                fileData[id] = (byte) (f.length() % 127);
            } catch (Exception e) {
                log.error("Error reading file: {} - {}", f.getName(), e.getMessage());
            }
        }
        return fileData;
    }

    private static final class SingletonHolder {
        private static final ResourcesData INSTANCE = new ResourcesData();
    }

    public static ResourcesData getInstance() {
        return ResourcesData.SingletonHolder.INSTANCE;
    }
}
