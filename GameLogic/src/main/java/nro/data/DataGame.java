package nro.data;

import nro.utils.FileNio;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataGame {

    private static DataGame instance;

    private static final Map<String, byte[]> DATA = new ConcurrentHashMap<>();

    private static final String BASE_PATH = "resources/data/update_data/";

    public static DataGame getInstance() {
        if (instance == null) {
            instance = new DataGame();
        }
        return instance;
    }

    public byte[] getDart() {
        return this.getData("NR_dart");
    }

    public byte[] getArrow() {
        return this.getData("NR_arrow");
    }

    public byte[] getEffect() {
        return this.getData("NR_effect");
    }

    public byte[] getImage() {
        return this.getData("NR_image");
    }

    public byte[] getSkill() {
        return this.getData("skill");
    }

    private byte[] getData(String key) {
        return DATA.computeIfAbsent(key, k -> FileNio.loadDataFile(BASE_PATH + k));
    }

}
