package nro.server.data_holders.data;

import nro.server.data_holders.IManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public final class VersionImageData implements IManager {

    private int[] versionImage;

    @Override
    public void init() throws IllegalArgumentException {
        setVersionImage();
    }

    @Override
    public void reload() throws IllegalArgumentException {
        setVersionImage();
    }

    @Override
    public void clear() {
        versionImage = null;
    }

    public void size() {

    }

    public int[] get() {
        return versionImage;
    }

    private void setVersionImage() throws IllegalArgumentException {
        versionImage = new int[4];
        for (int zoomLevel = 1; zoomLevel <= 4; zoomLevel++) {
            String path = "resources/x" + zoomLevel + "/res";
            versionImage[zoomLevel - 1] = countFiles(path);
        }
    }

    private int countFiles(String directoryPath) {
        try (Stream<Path> files = Files.walk(Paths.get(directoryPath))) {
            return (int) files.filter(Files::isRegularFile).count();
        } catch (Exception e) {
            throw new RuntimeException("Error counting files in directory: " + directoryPath, e);
        }
    }

    public int getVersionImage(int zoomLevel) {
        if (zoomLevel < 1 || zoomLevel > 4) {
            throw new RuntimeException("Zoom level must be between 1 and 4");
        }
        return versionImage[zoomLevel - 1];
    }

    private static final class SingletonHolder {
        private static final VersionImageData INSTANCE = new VersionImageData();
    }

    public static VersionImageData getInstance() {
        return SingletonHolder.INSTANCE;
    }

}
