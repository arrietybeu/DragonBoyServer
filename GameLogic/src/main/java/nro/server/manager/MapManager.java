package nro.server.manager;

import lombok.Getter;
import nro.model.map.TileMap;
import nro.model.map.Waypoint;
import nro.model.map.areas.Area;
import nro.model.map.decorates.BackgroudEffect;
import nro.model.map.decorates.BgItem;
import nro.model.monster.Monster;
import nro.model.npc.Npc;
import nro.model.template.map.TileSetTemplate;
import nro.network.Message;
import nro.server.config.ConfigDB;
import nro.repositories.DatabaseConnectionPool;
import nro.model.template.map.BackgroundMapTemplate;
import nro.model.map.GameMap;
import nro.server.LogServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.DataOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
@SuppressWarnings("ALL")
public class MapManager implements IManager {

    @Getter
    private static final MapManager instance = new MapManager();

    private final Map<Integer, GameMap> gameMaps = new HashMap<>();
    private final List<BackgroundMapTemplate> backgroundMapTemplates = new ArrayList<>();
    private final List<TileSetTemplate> tileSetTemplates = new ArrayList<>();

    private final ExecutorService threadPool = Executors.newFixedThreadPool(200);

    private byte[] BackgroundMapData;
    private byte[] TileSetData;

    @Override
    public void init() {
        this.loadMapTemplate();
        this.loadDataBackgroundMap();
        this.loadTileSetInfo();
        this.start();
    }

    @Override
    public void reload() {

        this.clear();
        this.init();
    }

    @Override
    public void clear() {
        this.threadPool.shutdown();
        this.gameMaps.clear();
        this.backgroundMapTemplates.clear();
        this.tileSetTemplates.clear();
        this.BackgroundMapData = null;
        this.TileSetData = null;
    }

    private void start() {
        try {
            for (GameMap map : this.gameMaps.values()) {
                this.threadPool.submit(map);
            }
        } catch (Exception ex) {
            LogServer.LogException("Error start thread pool Map: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadMapTemplate() {
        String query = "SELECT * FROM `map_template`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); var rs = ps.executeQuery()) {

            Map<Integer, TileMap> tileMaps = loadAllMapTiles(connection);

            while (rs.next()) {
                var id = rs.getInt("id");
                var name = rs.getString("name");
                var zone = rs.getByte("zone");
                var maxPlayer = rs.getByte("max_player");
                var type = rs.getByte("type");
                var planetId = rs.getByte("planet_id");
                var tileId = rs.getByte("tile_id");
                var bgId = rs.getByte("background_id");
                var bgType = rs.getByte("background_type");
                var isMapDouble = rs.getByte("is_map_double");

                List<BgItem> bgItems = this.loadItemBackgroundMap(connection, id);
                List<Waypoint> waypoints = this.loadWaypoints(connection, id);
                List<BackgroudEffect> effects = this.parseEffectMap(rs.getString("effect_map"));
                TileMap tileMap = tileMaps.get(id);

                GameMap mapTemplate = new GameMap(id, name, planetId, tileId, isMapDouble, type, bgId, bgType, bgItems, effects, waypoints, tileMap);
                mapTemplate.setAreas(this.initArea(connection, mapTemplate, zone, maxPlayer));
                this.gameMaps.put(id, mapTemplate);
            }

            LogServer.LogInit("MapManager init size: " + this.gameMaps.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadMap: " + e.getMessage());
        }
    }

    private Map<Integer, TileMap> loadAllMapTiles(Connection connection) {
        String query = "SELECT * FROM `map_tiles`";
        Map<Integer, TileMap> tileMaps = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(query); ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int mapId = rs.getInt("map_id");
                int tmw = rs.getInt("tmw");
                int tmh = rs.getInt("tmh");
                String mapsJson = rs.getString("tiles");

                int[] maps = parseJsonToIntArray(mapsJson);

                tileMaps.put(mapId, new TileMap(tmw, tmh, maps));
            }

            LogServer.LogInit("Loaded " + tileMaps.size() + " map tiles.");

        } catch (SQLException e) {
            LogServer.LogException("Error loading map tiles: " + e.getMessage());
        }
        return tileMaps;
    }

    private List<Area> initArea(Connection connection, GameMap map, int zone, int maxPlayer) {
        List<Area> areas = new ArrayList<>();
        for (int i = 0; i < zone; i++) {
            Area area = new Area(map, i, maxPlayer, this.loadMonsters(connection, map.getId()), this.loadNpcs(connection, map.getId()));
            areas.add(area);
        }
        return areas;
    }

    private Map<Integer, Monster> loadMonsters(Connection connection, int mapId) {
        Map<Integer, Monster> monsters = new HashMap<>();
        String query = "SELECT * FROM `map_monsters` WHERE map_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, mapId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Monster monster = new Monster();
                    monster.setId(rs.getInt("mob_id"));
                    monster.setSys(rs.getByte("sys"));
                    monster.setHp(rs.getLong("hp"));
                    monster.setLevel(rs.getByte("level"));
                    monster.setMaxp(rs.getLong("maxp"));
                    monster.setX(rs.getShort("x"));
                    monster.setY(rs.getShort("y"));
                    monster.setStatus(rs.getByte("status"));
                    monster.setLevelBoss(rs.getByte("level_boss"));
                    monster.setBoss(rs.getByte("is_boss") == 1);
                    monster.setDisable(rs.getByte("is_disable") == 1);
                    monster.setDontMove(rs.getByte("is_dont_move") == 1);
                    monster.setFire(rs.getByte("is_fire") == 1);
                    monster.setIce(rs.getByte("is_ice") == 1);
                    monster.setWind(rs.getByte("is_wind") == 1);
                    monsters.put(monster.getId(), monster);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadMonsters: " + e.getMessage());
        }
        return monsters;
    }

    private Map<Integer, Npc> loadNpcs(Connection connection, int mapId) {
        Map<Integer, Npc> npcs = new HashMap<>();
        String query = "SELECT * FROM `map_npc` WHERE map_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, mapId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Npc npc = new Npc();
                    npc.setTemplateId(rs.getInt("npc_id"));
                    npc.setStatus(rs.getByte("status"));
                    npc.setX(rs.getShort("x"));
                    npc.setY(rs.getShort("y"));
                    npc.setAvatar(rs.getShort("avatar"));
                    npcs.put(npc.getTemplateId(), npc);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadNpcs: " + e.getMessage());
        }
        return npcs;
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
            LogServer.LogException("Error loadWaypoints: " + e.getMessage());
        }
        return waypoints;
    }

    private void loadDataBackgroundMap() {
        String query = "SELECT * FROM `map_data_background`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); var rs = ps.executeQuery()) {
            while (rs.next()) {
                var bg = new BackgroundMapTemplate();
                bg.setId(rs.getInt("id"));
                bg.setImageId(rs.getShort("image"));
                bg.setLayer(rs.getByte("layer"));
                bg.setDx(rs.getShort("dx"));
                bg.setDy(rs.getShort("dy"));
                this.backgroundMapTemplates.add(bg);
            }
            this.setDataBackgroundMap();
            LogServer.LogInit("LoadItemBackgroundMap initialized size: " + this.backgroundMapTemplates.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadItemBackgroundMap: " + e.getMessage());
        }
    }

    private List<BgItem> loadItemBackgroundMap(Connection connection, int id) {
        String query = "SELECT * FROM `map_item_background` WHERE map_id = ?";
        List<BgItem> bgItems = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
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
        } catch (SQLException e) {
            LogServer.LogException("Error loadItemBackgroundMap: " + e.getMessage());
        }
        return bgItems;
    }

    private void loadTileSetInfo() {
        String query = "SELECT * FROM `map_tile_set_info`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(query);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                var tileSet = new TileSetTemplate();
                tileSet.setId(rs.getInt("id"));
                tileSet.setTile_type(rs.getByte("tile_type"));
                var tileTypes = this.loadTileType(connection, tileSet.getId());
                tileSet.setTileTypes(tileTypes);
                this.tileSetTemplates.add(tileSet);
            }
            LogServer.LogInit("LoadTileSetInfo initialized size: " + this.tileSetTemplates.size());
            this.setTileSetData();
        } catch (SQLException e) {
            LogServer.LogException("Error loadTileSetInfo: " + e.getMessage());
        }
    }

    private List<TileSetTemplate.TileType> loadTileType(Connection connection, int tileSetId) {
        String query = "SELECT * FROM `map_tile_type` WHERE tile_set_id = ?";
        List<TileSetTemplate.TileType> tileTypes = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, tileSetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var tileType = new TileSetTemplate.TileType();
                    tileType.setId(rs.getInt("id"));
                    tileType.setTileSetId(rs.getInt("tile_set_id"));
                    tileType.setTileTypeValue(rs.getInt("tile_type_value"));
                    tileType.setIndex(rs.getInt("so_index"));

                    String indexValueJson = rs.getString("index_value");
                    JSONArray dataArray = (JSONArray) JSONValue.parse(indexValueJson);

                    int[] indexValues = new int[dataArray.size()];
                    for (int i = 0; i < dataArray.size(); i++) {
                        indexValues[i] = ((Number) dataArray.get(i)).intValue();
                    }
                    tileType.setIndex_value(indexValues);
                    tileTypes.add(tileType); // add to the list
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadTileType: " + e.getMessage());
        } catch (Exception e) {
            LogServer.LogException("Error parsing index_value JSON: " + e.getMessage());
        }
        return tileTypes;
    }

    private void setDataBackgroundMap() {
        try (Message ms = new Message()) {
            try (DataOutputStream dataOutputStream = ms.writer()) {
                dataOutputStream.writeShort(this.backgroundMapTemplates.size());
                for (BackgroundMapTemplate bg : this.backgroundMapTemplates) {
                    dataOutputStream.writeShort(bg.getImageId());
                    dataOutputStream.writeByte(bg.getLayer());
                    dataOutputStream.writeShort(bg.getDx());
                    dataOutputStream.writeShort(bg.getDy());
                    dataOutputStream.writeByte(0);
                    /**
                     * dataOutputStream.writeShort(bg.tileX.length); for (int i = 0; i <
                     */
                }
                dataOutputStream.flush();
                this.BackgroundMapData = ms.getData();
            } catch (Exception e) {
                LogServer.LogException("Error setDataBackgroundMap: " + e.getMessage());
            }
        } catch (Exception e) {
            LogServer.LogException("Error setDataBackgroundMap: " + e.getMessage());
        }
    }

    private void setTileSetData() {
        try (Message ms = new Message()) {
            try (DataOutputStream dataOutputStream = ms.writer()) {
                List<TileSetTemplate> tileSetTemplates = this.tileSetTemplates;
                dataOutputStream.writeByte(tileSetTemplates.size());
                for (var tile : tileSetTemplates) {
                    dataOutputStream.writeByte(tile.getTile_type());
                    for (var tileType : tile.getTileTypes()) {
                        dataOutputStream.writeInt(tileType.getTileSetId());
                        dataOutputStream.writeByte(tileType.getIndex());

                        for (var indexValue : tileType.getIndex_value()) {
                            dataOutputStream.writeByte(indexValue);
                        }
                    }
                }
                dataOutputStream.flush();
                this.TileSetData = ms.getData();
            } catch (Exception e) {
                LogServer.LogException("Error setTileSetData: " + e.getMessage());
            }
        } catch (Exception e) {
            LogServer.LogException("Error setTileSetData: " + e.getMessage());
        }
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
            LogServer.LogException("Error parsing JSON: " + e.getMessage());
            return new int[0];
        }
    }

    private List<BackgroudEffect> parseEffectMap(String jsonEffect) {
        List<BackgroudEffect> effects = new ArrayList<>();

        if (jsonEffect == null || jsonEffect.trim().isEmpty()) {
            LogServer.LogException("Error json Effect by Map Template Is NUll");
            return effects;
        }

        try {
            JSONArray effectArray = (JSONArray) JSONValue.parseWithException(jsonEffect);
            for (Object obj : effectArray) {
                if (obj instanceof JSONArray) {
                    JSONArray eff = (JSONArray) obj;
                    if (eff.size() >= 2) {
                        String effectType = eff.get(0).toString();
                        String effectValue = eff.get(1).toString();
                        effects.add(new BackgroudEffect(effectType, effectValue));
                    }
                }
            }
        } catch (ParseException e) {
            LogServer.LogException("Error parsing effect_map JSON: " + e.getMessage());
        }

        return effects;
    }

    public GameMap findMapById(int id) {
        if (this.gameMaps.isEmpty()) {
            return null;
        }
        if (id < 0 || id >= this.gameMaps.size()) {
            return null;
        }
        return this.gameMaps.get(id);
    }

    public int sizeMap() {
        return this.gameMaps.size();
    }

}
