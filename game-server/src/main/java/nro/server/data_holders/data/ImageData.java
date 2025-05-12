package nro.server.data_holders.data;

import lombok.Getter;
import nro.server.data_holders.IManager;
import nro.server.data_holders.YamlDataLoader;

public final class ImageData implements IManager {

    @Getter
    private int[][] versionImage;

    @Override
    public void init() throws Throwable {
        versionImage = YamlDataLoader.loadArray("resources/data/update_data/NR_image.yml", int[][].class);
    }

    @Override
    public void reload() throws Throwable {
        init();
    }

    @Override
    public void clear() throws Throwable {
        versionImage = null;
    }

    private static final class SingletonHolder {
        private static final ImageData INSTANCE = new ImageData();
    }

    public static ImageData getInstance() {
        return ImageData.SingletonHolder.INSTANCE;
    }

}
