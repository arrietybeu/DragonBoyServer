package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.database.DatabaseFactory;
import nro.commons.database.DatabaseType;
import nro.server.data_holders.IManager;
import nro.server.model.templates.world.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arriety
 */
public class MapData implements IManager {

    private final static String QUERY_LOAD_MAP_TEMPLATE = "SELECT * FROM `map_template`";
    private final static String QUERY_LOAD_MAP_ITEM_BACKGROUND = "SELECT * FROM `map_item_background` WHERE `map_id` = ?";

    @Getter
    private final List<WorldMapTemplate> worldMaps = new ArrayList<>();

    @Override
    public void init() throws Throwable {
        loadMapTemplate();
    }

    @Override
    public void reload() throws Throwable {
    }

    @Override
    public void clear() throws Throwable {
        worldMaps.clear();
    }

    private void loadMapTemplate() {
        try (Connection con = DatabaseFactory.getConnection(DatabaseType.STATIC);
             PreparedStatement stmt = con.prepareStatement(QUERY_LOAD_MAP_TEMPLATE);
             var rs = stmt.executeQuery()) {

            Map<Short, TileMap> tileMaps = loadAllMapTiles(con);

            while (rs.next()) {
                var id = rs.getShort("id");
                var name = rs.getString("name");
                var zone = rs.getByte("zone");
                var maxPlayer = rs.getByte("max_player");
                var type = rs.getByte("type");
                var planetId = rs.getByte("planet_id");
                var tileId = rs.getByte("tile_id");
                var bgId = rs.getByte("background_id");
                var bgType = rs.getByte("background_type");
                var isMapDouble = rs.getByte("is_map_double");

                List<BgItem> bgItems = loadItemBackgroundMap(con, id);
                List<BackgroundEffect> effects = this.parseEffectMap(rs.getString("effect_map"));
                List<Waypoint> waypoints = this.loadWaypoints(con, id);
                TileMap tileMap = tileMaps.get(id);

                var worldMapTemplate = new WorldMapTemplate(
                        id, name, zone, maxPlayer, planetId,
                        tileId, isMapDouble, bgId, bgType, type,
                        bgItems, effects, waypoints, tileMap
                );
                worldMaps.add(worldMapTemplate);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Waypoint> loadWaypoints(Connection connection, int mapId) {
        String query = "SELECT * FROM `map_waypoint` WHERE map_id = ?";
        List<Waypoint> waypoints = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, mapId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Waypoint waypoint = new Waypoint();
                    waypoint.setName(rs.getString("name"));
                    waypoint.setMinX(rs.getShort("min_x"));
                    waypoint.setMinY(rs.getShort("min_y"));
                    waypoint.setMaxX(rs.getShort("max_x"));
                    waypoint.setMaxY(rs.getShort("max_y"));
                    waypoint.setEnter(rs.getByte("is_enter") == 1);
                    waypoint.setOffline(rs.getByte("is_offline") == 1);
                    waypoint.setGoMap(rs.getInt("go_map"));
                    waypoint.setGoX(rs.getShort("go_x"));
                    waypoint.setGoY(rs.getShort("go_y"));
                    waypoints.add(waypoint);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading waypoints for map id: " + mapId, e);
        }
        return waypoints;
    }

    private List<BackgroundEffect> parseEffectMap(String jsonEffect) {
        List<BackgroundEffect> effects = new ArrayList<>();

        if (jsonEffect == null || jsonEffect.trim().isEmpty()) {
            throw new RuntimeException("JSON effect is null or empty");
        }

        try {
            JSONArray effectArray = (JSONArray) JSONValue.parseWithException(jsonEffect);
            for (Object obj : effectArray) {
                if (obj instanceof JSONArray eff) {
                    if (eff.size() >= 2) {
                        String effectType = eff.get(0).toString();
                        String effectValue = eff.get(1).toString();
                        effects.add(new BackgroundEffect(effectType, effectValue));
                    }
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return effects;
    }

    private List<BgItem> loadItemBackgroundMap(Connection connection, int id) {
        List<BgItem> bgItems = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(QUERY_LOAD_MAP_ITEM_BACKGROUND)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BgItem bgItem = new BgItem();
                    bgItem.setId(rs.getInt("id"));
                    bgItem.setMapId(rs.getInt("map_id"));
                    bgItem.setX(rs.getInt("x"));
                    bgItem.setY(rs.getInt("y"));
                    bgItems.add(bgItem);
                }
            }
            return bgItems;
        } catch (SQLException e) {
            throw new RuntimeException("Error loading map item background for map id: " + id, e);
        }
    }


    private Map<Short, TileMap> loadAllMapTiles(Connection connection) {
        String query = "SELECT * FROM `map_tiles`";
        Map<Short, TileMap> tileMaps = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                short mapId = rs.getShort("map_id");
                int tmw = rs.getInt("width");
                int tmh = rs.getInt("height");
                String mapsJson = rs.getString("tiles");

                int[] maps = parseJsonToIntArray(mapsJson);

                tileMaps.put(mapId, new TileMap(tmw, tmh, maps));
            }

            // LogServer.LogInit("Loaded " + tileMaps.size() + " map tiles.");

        } catch (SQLException e) {
            throw new RuntimeException("Error loading map tiles", e);
        }
        return tileMaps;
    }

    private int[] parseJsonToIntArray(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(json);

            int[] maps = new int[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                maps[i] = Integer.parseInt(jsonArray.get(i).toString());
            }
            return maps;

        } catch (ParseException e) {
            throw new RuntimeException("Error parsing JSON to int array: " + json, e);
        }
    }

    private static final class SingletonHolder {
        private static final MapData INSTANCE = new MapData();
    }

    public static MapData getInstance() {
        return MapData.SingletonHolder.INSTANCE;
    }


}
