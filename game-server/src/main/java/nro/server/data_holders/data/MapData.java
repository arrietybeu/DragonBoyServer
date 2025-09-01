package nro.server.data_holders.data;

import lombok.Getter;
import nro.commons.database.Database;
import nro.commons.utils.NetworkUtils;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.GameEngine;
import nro.server.data_holders.YamlDataLoader;
import nro.server.model.templates.entity.NpcTemplate;
import nro.server.model.templates.world.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Arriety
 */
public final class MapData implements GameEngine {

    private final static String QUERY_LOAD_MAP_TEMPLATE = "SELECT * FROM `map_template`";
    private final static String QUERY_LOAD_MAP_ITEM_BACKGROUND = "SELECT * FROM `map_item_background` WHERE `map_id` = ?";

    private final static byte MAX_TILE_SET = 33;

    private List<BackgroundMapTemplate> backgroundMapTemplates;

    @Getter
    private byte[] dataBackgroundMapTemplates;

    public int[][] tileType = new int[MAX_TILE_SET][];
    public int[][][] tileIndex = new int[MAX_TILE_SET][][];
    public byte[] tileSetInfoData;
    public byte[] dataMapData;

    @Getter
    private final Map<Short, WorldMapTemplate> worldMaps = new LinkedHashMap<>();

    @Override
    public void init() throws Throwable {
        Database.withConnection(connection -> {
            loadMapTemplate(connection);
            loadTileSetInfo(connection);
            return null;
        });
        setQueryLoadMapItemBackground();
        setUpdateDataMap();
    }

    @Override
    public void reload() throws Throwable {
        clear();
        this.init();
    }

    @Override
    public void clear() throws Throwable {
        worldMaps.clear();
    }

    private void loadMapTemplate(Connection connection) {

        Map<Short, TileMap> tileMaps = loadAllMapTiles(connection);

        Database.select(connection, QUERY_LOAD_MAP_TEMPLATE, rs -> {
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

                List<BgItem> bgItems = loadItemBackgroundMap(connection, id);
                List<BackgroundEffect> effects = this.parseEffectMap(rs.getString("effect_map"));
                List<Waypoint> waypoints = this.loadWaypoints(connection, id);
                TileMap tileMap = tileMaps.get(id);
                List<NpcTemplate.NpcInfo> npcs = this.loadNpcs(connection, id);

                var worldMapTemplate = new WorldMapTemplate(id, name, zone, maxPlayer, planetId, tileId, isMapDouble, bgId, bgType, type, bgItems, effects, waypoints, tileMap, npcs);
                worldMaps.put(id, worldMapTemplate);
            }
        });
    }

    private List<NpcTemplate.NpcInfo> loadNpcs(Connection con, int mapID) {
        String query = "SELECT * FROM `map_npc` WHERE map_id = ?";
        List<NpcTemplate.NpcInfo> npcs = new ArrayList<>();
        Database.select(con, query, rs -> {
            while (rs.next()) {
                var id = rs.getInt("npc_id");
                var status = rs.getByte("status");
                var x = rs.getShort("x");
                var y = rs.getShort("y");
                var avatar = rs.getShort("avatar");

                NpcTemplate template = NpcData.getInstance().getTemplateById(id);
                var npc = new NpcTemplate.NpcInfo(id, x, y, status, avatar, template);
                npcs.add(npc);
            }
        }, preparedStatement -> preparedStatement.setInt(1, mapID));
        return npcs;
    }

    private List<Waypoint> loadWaypoints(Connection connection, int mapId) {
        String query = "SELECT * FROM `map_waypoint` WHERE map_id = ?";
        List<Waypoint> waypoints = new ArrayList<>();
        Database.select(connection, query, rs -> {
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

    private List<BgItem> loadItemBackgroundMap(Connection con, int id) {
        List<BgItem> bgItems = new ArrayList<>();
        Database.select(con, QUERY_LOAD_MAP_ITEM_BACKGROUND, rs -> {
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

    private Map<Short, TileMap> loadAllMapTiles(Connection connection) {
        String query = "SELECT * FROM `map_tiles`";
        Map<Short, TileMap> tileMaps = new HashMap<>();

        Database.select(connection, query, rs -> {
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


    private void setQueryLoadMapItemBackground() {
        backgroundMapTemplates = YamlDataLoader.loadList("resources/data/update_data/NR_map_data_background.yml", BackgroundMapTemplate.class);
        setDataBackgroundMapTemplates();
    }

    private void setDataBackgroundMapTemplates() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        buf.putShort((short) this.backgroundMapTemplates.size());
        for (var template : backgroundMapTemplates) {
            buf.putShort(template.getImage());
            buf.put(template.getLayer());
            buf.putShort(template.getDx());
            buf.putShort(template.getDy());
            buf.put((byte) 0);
        }
        buf.flip();
        dataBackgroundMapTemplates = new byte[buf.remaining()];
        buf.get(dataBackgroundMapTemplates);

        backgroundMapTemplates.clear();
        backgroundMapTemplates = null; // Clear reference to free memory
    }

    private void loadTileSetInfo(Connection connection) {
        List<Byte> tileSetIds = new ArrayList<>();
        Database.select(connection, "SELECT DISTINCT tile_set_id FROM tile_types", rs -> {
            while (rs.next()) {
                tileSetIds.add(rs.getByte("tile_set_id"));
            }
        });
        setTileSetInfoData(connection, tileSetIds);
    }

    private void setTileSetInfoData(Connection connectiono, List<Byte> tileSetIds) {
        ByteBuffer buf = ByteBuffer.allocate(100_000);

        byte count = (byte) tileSetIds.size();

        buf.put(count);

        for (int idx = 0; idx < count; idx++) {

            int tileSetId = tileSetIds.get(idx);

            List<Integer> typeList = new ArrayList<>();
            List<int[]> indexList = new ArrayList<>();

            var query = "SELECT type_value, GROUP_CONCAT(index_value ORDER BY index_value) AS indices " + "FROM tile_types WHERE tile_set_id = ? GROUP BY type_value";

            Database.select(connectiono, query, rs -> {
                while (rs.next()) {
                    int typeVal = rs.getInt("type_value");
                    String[] indicesStr = rs.getString("indices").split(",");
                    int[] indices = new int[indicesStr.length];
                    for (int i = 0; i < indicesStr.length; i++) {
                        indices[i] = Integer.parseInt(indicesStr[i]);
                    }
                    typeList.add(typeVal);
                    indexList.add(indices);
                }

            }, ps -> ps.setInt(1, tileSetId));

            buf.put((byte) typeList.size());
            for (int i = 0; i < typeList.size(); i++) {
                int type = typeList.get(i);
                int[] indices = indexList.get(i);
                buf.putInt(type); // type_value
                buf.put((byte) indices.length); // số index
                for (int idxVal : indices) {
                    buf.put((byte) idxVal); // từng index
                }
            }

            tileType[idx] = typeList.stream().mapToInt(i -> i).toArray();
            tileIndex[idx] = indexList.toArray(new int[0][]);
        }

        buf.flip();

        tileSetInfoData = new byte[buf.remaining()];
        buf.get(tileSetInfoData);
    }

    private void setUpdateDataMap() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);

        var mapTemplates = this.worldMaps.values();
        buf.put((byte) ConfigServer.VERSION_DATA_MAP);
        buf.putShort((short) mapTemplates.size());

        for (var map : mapTemplates) {
            NetworkUtils.writeString(buf, map.getName());
        }

        var npcTemplates = NpcData.getInstance().getNpcTemplates();

        buf.put((byte) npcTemplates.size());
        for (var npc : npcTemplates) {
            NetworkUtils.writeString(buf, npc.name());
            buf.putShort((short) npc.head());
            buf.putShort((short) npc.body());
            buf.putShort((short) npc.leg());
            buf.put((byte) 0);
//            this.writeByte(1);
//            this.writeUTF("Nói chuyện");
        }

        var monsterTemplates = MonsterData.getInstance().getMonsters();
        buf.putShort((short) monsterTemplates.size());// client version thap send byte
        for (var monster : monsterTemplates) {
            buf.put(monster.type());
            NetworkUtils.writeString(buf, monster.NAME());
            buf.putLong(monster.hp());
            buf.put(monster.rangeMove());
            buf.put(monster.speed());
            buf.put(monster.dartType());
        }

        buf.flip();
        dataMapData = new byte[buf.remaining()];
        buf.get(dataMapData);
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
