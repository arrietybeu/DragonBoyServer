package nro.server.dao;

import com.artemis.World;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.item.*;
import nro.server.model.ecs.component.player.InventoryComponent;
import nro.server.model.item.ItemOptionData;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arriety
 */
public class InventoryDAO {

    private static final Logger log = LoggerFactory.getLogger(InventoryDAO.class);
    private static final String SELECT_QUERY = """
            SELECT id, template_id, quantity, options FROM `player_inventory` WHERE `player_id`= ? AND `location`= ? ORDER BY `row_index` ASC
            """;

    public static void loadInventoryForPlayer(Connection conn, int playerEntityId) throws SQLException {
        InventoryComponent playerInventory = new InventoryComponent();
        GameWorld.getInstance().getWorld().edit(playerEntityId).add(playerInventory);

        loadItemsForLocation(conn, playerEntityId, ItemLocation.BODY);
        loadItemsForLocation(conn, playerEntityId, ItemLocation.BAG);
        loadItemsForLocation(conn, playerEntityId, ItemLocation.BOX);
    }

    private static void loadItemsForLocation(Connection conn, int playerEntityId, ItemLocation location) throws SQLException {
        World world = GameWorld.getInstance().getWorld();
        InventoryComponent playerInventory = world.getMapper(InventoryComponent.class).get(playerEntityId);
        try (PreparedStatement ps = conn.prepareStatement(SELECT_QUERY)) {
            ps.setInt(1, playerEntityId);
            ps.setInt(2, location.getType());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int templateId = rs.getInt("template_id");
                    if (templateId == -1) continue;

                    int itemEntityId = world.create();
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
                            log.error("Failed to parse item options for player entity ID: {} and template ID: {}. Options JSON: {}", playerEntityId, templateId, optionsJson, e);
                        }
                    }

                    switch (location) {
                        case BODY -> playerInventory.itemsBody.add(itemEntityId);
                        case BAG -> playerInventory.itemsBag.add(itemEntityId);
                        case BOX -> playerInventory.itemsBox.add(itemEntityId);
                    }
                }
            }
        }
    }

}
