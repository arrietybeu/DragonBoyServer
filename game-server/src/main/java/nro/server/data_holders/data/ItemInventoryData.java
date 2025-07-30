package nro.server.data_holders.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import nro.server.data_holders.GameEngine;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author Arriety
 */
public class ItemInventoryData implements GameEngine {
    private List<Map<String, Object>> templates = new ArrayList<>();

    @Override
    public void init() throws Throwable {
        try (InputStream input = new FileInputStream("resources/data_holder/player_inventory.yml")) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            templates = mapper.readValue(input, mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
            validateTemplates();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load YAML from resources/data_holder/player_inventory.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void validateTemplates() {
        if (templates == null || templates.isEmpty()) {
            throw new RuntimeException("YAML data is empty or null");
        }
        for (Map<String, Object> template : templates) {
            if (!template.containsKey("gender")) {
                throw new RuntimeException("Missing 'gender' field in YAML data");
            }
            Map<String, Object> items = (Map<String, Object>) template.get("items");
            if (items == null) {
                throw new RuntimeException("Missing 'items' field for gender: " + template.get("gender"));
            }
            for (String location : List.of("body", "bag", "box")) {
                List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get(location);
                if (itemList != null) {
                    for (int i = 0; i < itemList.size(); i++) {
                        Map<String, Object> item = itemList.get(i);
                        if (!item.containsKey("template_id")) {
                            throw new RuntimeException("Missing 'template_id' for item at index " + i + " in " + location + " for gender: " + template.get("gender"));
                        }
                        if (!item.containsKey("quantity")) {
                            throw new RuntimeException("Missing 'quantity' for item at index " + i + " in " + location + " for gender: " + template.get("gender"));
                        }
                        if (!item.containsKey("options")) {
                            throw new RuntimeException("Missing 'options' for item at index " + i + " in " + location + " for gender: " + template.get("gender"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void reload() throws Throwable {
        clear();
        init();
    }

    @Override
    public void clear() throws Throwable {
        if (templates != null) templates.clear();
        templates = null;
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getItemsByGender(byte gender, String location) throws RuntimeException {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> template : templates) {
            if ((int) template.get("gender") == gender) {
                Map<String, Object> items = (Map<String, Object>) template.get("items");
                if (items != null) {
                    List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get(location);
                    if (itemList != null) {
                        Set<Integer> usedRows = new HashSet<>();
                        for (Map<String, Object> item : itemList) {
                            if (!item.containsKey("row")) {
                                throw new RuntimeException("Missing 'row' field for item in " + location + " for gender: " + gender);
                            }
                            int rowIndex = (int) item.get("row");
                            if (rowIndex < 0) {
                                throw new RuntimeException("Invalid negative 'row' (" + rowIndex + ") for item in " + location + " for gender: " + gender);
                            }
                            if (usedRows.contains(rowIndex)) {
                                throw new RuntimeException("Duplicate 'row' (" + rowIndex + ") for item in " + location + " for gender: " + gender);
                            }
                            usedRows.add(rowIndex);
                            item.put("row_index", rowIndex);
                        }
                        result.addAll(itemList);
                    }
                }
                break;
            }
        }
        return result;
    }

    private static class SingletonHolder {
        private static final ItemInventoryData INSTANCE = new ItemInventoryData();
    }

    public static ItemInventoryData getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
