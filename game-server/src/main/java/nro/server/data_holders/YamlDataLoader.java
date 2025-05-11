package nro.server.data_holders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arriety
 */
public class YamlDataLoader {

    public static <T> List<T> loadList(String filePath, Class<T> elementClass) {
        try (InputStream input = new FileInputStream(filePath)) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(input,
                    mapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML list from " + filePath, e);
        }
    }

    public static <K, V> Map<K, V> loadMap(String filePath, Class<K> keyClass, Class<V> valueClass) {
        try (InputStream input = new FileInputStream(filePath)) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(input,
                    mapper.getTypeFactory().constructMapType(LinkedHashMap.class, keyClass, valueClass));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML map from " + filePath, e);
        }
    }


}
