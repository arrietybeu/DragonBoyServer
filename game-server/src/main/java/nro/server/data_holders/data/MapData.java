package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.database.Database;
import nro.server.data_holders.IManager;
import nro.server.model.templates.world.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Arriety
 */
public final class MapData implements IManager {

    private final static String QUERY_LOAD_MAP_TEMPLATE = "SELECT * FROM `map_template`";
    private final static String QUERY_LOAD_MAP_ITEM_BACKGROUND = "SELECT * FROM `map_item_background` WHERE `map_id` = ?";

//    private final List<WorldMapTemplate> worldMaps = new ArrayList<>();

    @Getter
    private final Map<Short, WorldMapTemplate> worldMaps = new LinkedHashMap<>();

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
        Database.select(QUERY_LOAD_MAP_TEMPLATE, rs -> {
            Map<Short, TileMap> tileMaps = loadAllMapTiles();

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

                List<BgItem> bgItems = loadItemBackgroundMap(id);
                List<BackgroundEffect> effects = this.parseEffectMap(rs.getString("effect_map"));
                List<Waypoint> waypoints = this.loadWaypoints(id);
                TileMap tileMap = tileMaps.get(id);

                var worldMapTemplate = new WorldMapTemplate(
                        id, name, zone, maxPlayer, planetId,
                        tileId, isMapDouble, bgId, bgType, type,
                        bgItems, effects, waypoints, tileMap
                );
                worldMaps.put(id, worldMapTemplate);
            }
        });
    }

    private List<Waypoint> loadWaypoints(int mapId) {
        String query = "SELECT * FROM `map_waypoint` WHERE map_id = ?";
        List<Waypoint> waypoints = new ArrayList<>();
        Database.select(query, rs -> {
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
        }, preparedStatement -> preparedStatement.setInt(1, mapId));
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

    private List<BgItem> loadItemBackgroundMap(int id) {
        List<BgItem> bgItems = new ArrayList<>();
        Database.select(QUERY_LOAD_MAP_ITEM_BACKGROUND, rs -> {
            while (rs.next()) {
                BgItem bgItem = new BgItem();
                bgItem.setId(rs.getInt("id"));
                bgItem.setMapId(rs.getInt("map_id"));
                bgItem.setX(rs.getInt("x"));
                bgItem.setY(rs.getInt("y"));
                bgItems.add(bgItem);
            }
        }, preparedStatement -> preparedStatement.setInt(1, id));
        return bgItems;
    }

    private Map<Short, TileMap> loadAllMapTiles() {
        String query = "SELECT * FROM `map_tiles`";
        Map<Short, TileMap> tileMaps = new HashMap<>();

        Database.select(query, rs -> {
            while (rs.next()) {
                short mapId = rs.getShort("map_id");
                int tmw = rs.getInt("width");
                int tmh = rs.getInt("height");
                String mapsJson = rs.getString("tiles");

                int[] maps = parseJsonToIntArray(mapsJson);
                tileMaps.put(mapId, new TileMap(tmw, tmh, maps));
            }
        });
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

    public void forEachParalllel(Consumer<WorldMapTemplate> consumer) {
        worldMaps.values().parallelStream().forEach(consumer);
    }

    private static final class SingletonHolder {
        private static final MapData INSTANCE = new MapData();
    }

    public static MapData getInstance() {
        return MapData.SingletonHolder.INSTANCE;
    }


}
