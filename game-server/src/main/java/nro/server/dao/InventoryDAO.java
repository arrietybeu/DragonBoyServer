package nro.server.dao;

import com.artemis.Entity;
import com.artemis.World;
import nro.server.configs.main.ConfigCharacter;
import nro.server.data_holders.data.ItemInventoryData;
import nro.server.engine.entity.GameWorld;
import nro.server.model.ecs.component.InfoComponent;
import nro.server.model.ecs.component.item.*;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.item.ItemOptionData;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * @author Arriety
 */
public class InventoryDAO {

    // FIXME WARNING: Class này chưa hoàn thiện logic id item template với id item entity đang sai

    private static final Logger log = LoggerFactory.getLogger(InventoryDAO.class);
    private static final String SELECT_QUERY = """
            SELECT id, template_id, quantity, options, row_index, creator_id FROM `player_inventory` WHERE `player_id`= ? AND `location`= ? ORDER BY `row_index` ASC
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO player_inventory (player_id, template_id, quantity, options, location, row_index, creator_id, create_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    public static void createPlayerInventory(Connection connection, int playerId, byte gender) throws SQLException {
        ItemInventoryData itemTemplateData = ItemInventoryData.getInstance();
        List<Map<String, Object>> itemsBody = itemTemplateData.getItemsByGender(gender, "body");
        List<Map<String, Object>> itemsBag = itemTemplateData.getItemsByGender(gender, "bag");
        List<Map<String, Object>> itemsBox = itemTemplateData.getItemsByGender(gender, "box");

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        validateRows(itemsBody, ConfigCharacter.INVENTORY_BODY_SIZE, "body", gender);
        validateRows(itemsBag, ConfigCharacter.INVENTORY_BAG_SIZE, "bag", gender);
        validateRows(itemsBox, ConfigCharacter.INVENTORY_BOX_SIZE, "box", gender);

        for (Map<String, Object> item : itemsBody) {
            item.put("create_time", currentTime);
            item.put("location", 0);
        }
        for (Map<String, Object> item : itemsBag) {
            item.put("create_time", currentTime);
            item.put("location", 1);
        }
        for (Map<String, Object> item : itemsBox) {
            item.put("create_time", currentTime);
            item.put("location", 2);
        }

        List<Map<String, Object>> allItems = new ArrayList<>();
        allItems.addAll(itemsBody);
        allItems.addAll(itemsBag);
        allItems.addAll(itemsBox);

        insertItemsToDatabase(connection, playerId, allItems);
    }

    private static void validateRows(List<Map<String, Object>> items, int maxSlots, String location, byte gender) throws SQLException {
        for (Map<String, Object> item : items) {
            int rowIndex = (int) item.get("row_index");
            if (rowIndex >= maxSlots) {
                throw new SQLException("Row " + rowIndex + " exceeds max slots " + maxSlots + " in " + location + " for gender: " + gender);
            }
        }
    }

    private static void insertItemsToDatabase(Connection connection, int playerId, List<Map<String, Object>> items) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
            for (Map<String, Object> item : items) {
                List<String> missingFields = new ArrayList<>();
                if (!item.containsKey("template_id")) missingFields.add("template_id");
                if (!item.containsKey("quantity")) missingFields.add("quantity");
                if (!item.containsKey("options")) missingFields.add("options");
                if (!item.containsKey("location")) missingFields.add("location");
                if (!item.containsKey("row_index")) missingFields.add("row_index");
                if (!item.containsKey("create_time")) missingFields.add("create_time");
                if (!missingFields.isEmpty()) {
                    throw new SQLException("Missing fields " + missingFields + " for item in player inventory for player ID: " + playerId);
                }

                statement.setInt(1, playerId);
                statement.setInt(2, (int) item.get("template_id"));
                statement.setInt(3, (int) item.get("quantity"));
                statement.setString(4, (String) item.get("options"));
                statement.setInt(5, (int) item.get("location"));
                statement.setInt(6, (int) item.get("row_index"));
                statement.setInt(7, playerId);
                statement.setTimestamp(8, (Timestamp) item.get("create_time"));
                statement.addBatch();
            }
            int[] rowsAffected = statement.executeBatch();
            if (Arrays.stream(rowsAffected).sum() == 0) {
                throw new SQLException("Failed to insert items into player_inventory for player ID: " + playerId);
            }
        }
    }

    public static void loadInventoryForPlayer(Connection conn, Entity entity, int playerId) throws SQLException {
        InventoryComponent playerInventory = new InventoryComponent();
        entity.edit().add(playerInventory);

        loadItemsForLocation(conn, entity.getId(), playerId, ItemLocation.BODY);
        loadItemsForLocation(conn, entity.getId(), playerId, ItemLocation.BAG);
        loadItemsForLocation(conn, entity.getId(), playerId, ItemLocation.BOX);

        System.out.println("Loaded inventory for player ID: " + playerId +
                ", Body items: " + playerInventory.itemsBody.size() +
                ", Bag items: " + playerInventory.itemsBag.size() +
                ", Box items: " + playerInventory.itemsBox.size());
        playerInventory.isDirty = true; // Mark inventory as dirty after loading

        GameWorld.getInstance().getWorld().process();
    }

    private static void loadItemsForLocation(Connection conn, int playerEntityId, int playerID, ItemLocation location) throws SQLException {
        World world = GameWorld.getInstance().getWorld();
        InventoryComponent playerInventory = world.getMapper(InventoryComponent.class).get(playerEntityId);
        var info = world.getEntity(playerEntityId).getComponent(InfoComponent.class);
        int maxSlots = switch (location) {
            case BODY -> ConfigCharacter.INVENTORY_BODY_SIZE;
            case BAG -> info.maxBagSize;
            case BOX -> info.maxBoxSize;
        };
        List<Integer> inventorySlots = new ArrayList<>(Collections.nCopies(maxSlots, -1));
        try (PreparedStatement ps = conn.prepareStatement(SELECT_QUERY)) {
            ps.setInt(1, playerID);
            ps.setInt(2, location.getType());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int templateId = rs.getInt("template_id");
                    if (templateId == -1) continue;
                    int rowIndex = rs.getInt("row_index");
                    if (rowIndex < 0 || rowIndex >= maxSlots) {
                        log.error("Invalid row_index {} for item in location {} for player ID {}", rowIndex, location, playerID);
                        continue;
                    }
                    if (inventorySlots.get(rowIndex) != -1) {
                        log.error("Duplicate row_index {} in location {} for player ID {}", rowIndex, location, playerID);
                        continue;
                    }
                    int itemEntityId = GameWorld.getInstance().createEntity();
                    var editor = world.edit(itemEntityId);

                    editor.add(new ItemInfoComponent(templateId, rs.getInt("quantity"), rs.getInt("creator_id")));
                    editor.add(new OwnershipComponent(playerEntityId, location));

                    String optionsJson = rs.getString("options");
                    if (optionsJson != null && !optionsJson.isEmpty() && !optionsJson.equals("[]")) {
                        try {
                            JSONArray jsonArray = (JSONArray) new JSONParser().parse(optionsJson);
                            ItemStatsComponent stats = new ItemStatsComponent();
                            for (Object obj : jsonArray) {
                                JSONArray optionArray = (JSONArray) obj;
                                stats.options.add(new ItemOptionData(((Number) optionArray.get(0)).shortValue(), ((Number) optionArray.get(1)).intValue(), (short) 0));
                            }
                            editor.add(stats);
                        } catch (Exception e) {
                            log.error("Failed to parse item options for player entity ID: {} and template ID: {}. Options JSON: {}", playerID, templateId, optionsJson, e);
                        }
                    }
                    inventorySlots.set(rowIndex, itemEntityId);
                }
            }
        }

        switch (location) {
            case BODY -> playerInventory.itemsBody = inventorySlots;
            case BAG -> playerInventory.itemsBag = inventorySlots;
            case BOX -> playerInventory.itemsBox = inventorySlots;
        }
    }


}