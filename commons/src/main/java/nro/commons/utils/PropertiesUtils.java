package nro.commons.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Properties;

public class PropertiesUtils {

    public static Properties load(String filename) throws IOException {
        return load(new File(filename), null);
    }

    public static Properties load(String filename, Properties defaults) throws IOException {
        return load(new File(filename), defaults);
    }

    public static Properties load(File file, Properties defaults) throws IOException {
        Properties p = new Properties(defaults);
        if (file.isFile())
            loadProperties(p, file);
        return p;
    }

    private static void loadProperties(Properties properties, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new IOException("Could not parse " + file, e);
        }
    }

    public static void loadFromDirectory(Properties properties, String dir, boolean recursive) throws IOException {
        loadFromDirectory(properties, new File(dir), recursive);
    }

    public static void loadFromDirectory(Properties properties, File dir, boolean recursive) throws IOException {
        for (Iterator<File> iter = propertiesFileIterator(dir, recursive); iter.hasNext(); ) {
            loadProperties(properties, iter.next());
        }
    }

    private static Iterator<File> propertiesFileIterator(File dir, boolean recursive) throws IOException {
        return Files.walk(dir.toPath(), recursive ? Integer.MAX_VALUE : 1)
                .filter(p -> p.toString().endsWith(".properties") && p.toFile().isFile())
                .map(Path::toFile)
                .iterator();
    }

}
