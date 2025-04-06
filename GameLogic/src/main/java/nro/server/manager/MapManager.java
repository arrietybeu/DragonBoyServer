package nro.server.manager;

import lombok.Getter;
import nro.consts.ConstMap;
import nro.consts.ConstPlayer;
import nro.consts.ConstTypeObject;
import nro.server.service.model.map.TileMap;
import nro.server.service.model.map.Waypoint;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.map.decorates.BackgroudEffect;
import nro.server.service.model.map.decorates.BgItem;
import nro.server.service.model.entity.monster.Monster;
import nro.server.service.model.template.NpcTemplate;
import nro.server.service.model.template.map.TileSetTemplate;
import nro.server.network.Message;
import nro.server.config.ConfigDB;
import nro.server.service.model.template.map.Transport;
import nro.server.service.repositories.DatabaseFactory;
import nro.server.service.model.template.map.BackgroundMapTemplate;
import nro.server.service.model.map.GameMap;
import nro.server.system.LogServer;
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

@Getter
@SuppressWarnings("ALL")
public final class MapManager implements IManager {

    @Getter
    private static final MapManager instance = new MapManager();
    private final Map<Integer, GameMap> gameMaps = new HashMap<>();
    private final List<BackgroundMapTemplate> backgroundMapTemplates = new ArrayList<>();
    private final List<TileSetTemplate> tileSetTemplates = new ArrayList<>();
    private final List<Transport> transports = new ArrayList<>();
    private byte[] BackgroundMapData;
    private byte[] TileSetData;

    private final String SELECT_MAP_TRANSPORT = "SELECT * FROM `map_transport` ORDER BY index_row";

    @Override
    public void init() {
        this.loadMapTemplate();
        this.loadDataBackgroundMap();
        this.loadTileSetInfo();
        this.loadTransportsMap();
    }

    @Override
    public void reload() {
        this.clear();
        this.init();
    }

    @Override
    public void clear() {
        this.gameMaps.clear();
        this.backgroundMapTemplates.clear();
        this.tileSetTemplates.clear();
        this.transports.clear();
        this.BackgroundMapData = null;
        this.TileSetData = null;
    }

    private void loadMapTemplate() {
        String query = "SELECT * FROM `map_template`";
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(query);
             var rs = ps.executeQuery()) {

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
                List<NpcTemplate.NpcInfo> npcs = this.loadNpcs(connection, id);

                if (type == ConstMap.MAP_OFFLINE) maxPlayer = 1;

                GameMap mapTemplate = new GameMap(id, name, planetId, tileId, isMapDouble, bgId, bgType, type, bgItems,
                        effects, waypoints, tileMap, npcs);
                mapTemplate.setAreas(this.initArea(connection, mapTemplate, zone, maxPlayer));
                mapTemplate.initNpc();
                this.gameMaps.put(id, mapTemplate);
            }

            // LogServer.LogInit("MapManager init size: " + this.gameMaps.size());
        } catch (Exception e) {
            e.printStackTrace();
            LogServer.LogException("Error loadMap: " + e.getMessage());
        }
    }

    private Map<Integer, TileMap> loadAllMapTiles(Connection connection) {
        String query = "SELECT * FROM `map_tiles`";
        Map<Integer, TileMap> tileMaps = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int mapId = rs.getInt("map_id");
                int tmw = rs.getInt("width");
                int tmh = rs.getInt("height");
                String mapsJson = rs.getString("tiles");

                int[] maps = parseJsonToIntArray(mapsJson);

                tileMaps.put(mapId, new TileMap(tmw, tmh, maps));
            }

            // LogServer.LogInit("Loaded " + tileMaps.size() + " map tiles.");

        } catch (SQLException e) {
            LogServer.LogException("Error loading map tiles: " + e.getMessage());
        }
        return tileMaps;
    }

    private List<Area> initArea(Connection connection, GameMap map, int zone, int maxPlayer) {
        List<Area> areas = new ArrayList<>();
        for (int i = 0; i < zone; i++) {
            Area area = new Area(map, i, maxPlayer);
            area.setMonsters(this.loadMonsters(connection, area));
            areas.add(area);
        }
        return areas;
    }

    private Map<Integer, Monster> loadMonsters(Connection connection, Area area) {
        Map<Integer, Monster> monsters = new HashMap<>();
        String query = "SELECT * FROM `map_monsters` WHERE map_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, area.getMap().getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var idTemplate = (rs.getInt("mob_id"));
                    var hpMax = (rs.getLong("max_hp"));
                    var level = (rs.getByte("level"));
                    var x = (rs.getShort("x"));
                    var y = (rs.getShort("y"));
                    var id = monsters.size();
                    Monster monster = new Monster(idTemplate, id, hpMax, level, x, y, area);
                    monsters.put(id, monster);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadMonsters: " + e.getMessage());
        }
        return monsters;
    }

    private List<NpcTemplate.NpcInfo> loadNpcs(Connection connection, int mapId) {
        List<NpcTemplate.NpcInfo> npcs = new ArrayList<>();
        String query = "SELECT * FROM `map_npc` WHERE map_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, mapId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    var id = rs.getInt("npc_id");
                    var status = rs.getByte("status");
                    var x = rs.getShort("x");
                    var y = rs.getShort("y");
                    var avatar = rs.getShort("avatar");
                    var npc = new NpcTemplate.NpcInfo(id, x, y, status, avatar);
                    npcs.add(npc);
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
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(query);
             var rs = ps.executeQuery()) {
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
            // LogServer.LogInit("LoadItemBackgroundMap initialized size: " +
            // this.backgroundMapTemplates.size());
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
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(query);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                var tileSet = new TileSetTemplate();
                tileSet.setId(rs.getInt("id"));
                tileSet.setTileType(rs.getByte("tile_type"));
                var tileTypes = this.loadTileType(connection, tileSet.getId());
                tileSet.setTileTypes(tileTypes);
                this.tileSetTemplates.add(tileSet);
            }
            // LogServer.LogInit("LoadTileSetInfo initialized size: " +
            // this.tileSetTemplates.size());
            this.setTileSetData();
        } catch (SQLException e) {
            LogServer.LogException("Error loadTileSetInfo: " + e.getMessage(), e);
        }
    }

    private void loadTransportsMap() {
        try (Connection connection = DatabaseFactory.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(SELECT_MAP_TRANSPORT); var rs = ps.executeQuery()) {
            while (rs.next()) {
                Transport transport = new Transport();
                var mapIds = rs.getString("map_id");
                var nameMap = rs.getString("name");
                var planetName = rs.getString("planet_name");
                var x = rs.getShort("x");
                var y = rs.getShort("y");

                JSONArray planetNameArray = (JSONArray) JSONValue.parse(planetName);
                JSONArray mapIdArray = (JSONArray) JSONValue.parse(mapIds);

                transport.setName(nameMap);
                transport.setPlanetName(new String[planetNameArray.size()]);
                transport.setMapIds(new short[mapIdArray.size()]);
                transport.setX(x);
                transport.setY(y);

                for (int i = 0; i < planetNameArray.size(); i++) {
                    transport.getPlanetName()[i] = planetNameArray.get(i).toString();
                }

                for (int i = 0; i < mapIdArray.size(); i++) {
                    transport.getMapIds()[i] = Short.parseShort(mapIdArray.get(i).toString());
                }

                this.transports.add(transport);
            }
        } catch (Exception ex) {
            LogServer.LogException("Error loadTransportsMap: " + ex.getMessage(), ex);
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
                    tileType.setIndexValue(indexValues);
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
                    dataOutputStream.writeByte(tile.getTileType());
                    for (var tileType : tile.getTileTypes()) {
                        dataOutputStream.writeInt(tileType.getTileSetId());
                        dataOutputStream.writeByte(tileType.getIndex());
                        for (var indexValue : tileType.getIndexValue()) {
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
        if (this.gameMaps.isEmpty()) return null;
        if (id < 0 || id >= this.gameMaps.size()) return null;
        return this.gameMaps.get(id);
    }

    public String getNameMapById(int id) {
        GameMap map = this.findMapById(id);
        if (map == null) {
            return "Unknown";
        }
        return map.getName();
    }

    public int sizeMap() {
        return this.gameMaps.size();
    }


    public int checkAllPlayerInGame() {
        int size = 0;
        for (GameMap map : this.gameMaps.values()) {
            for (Area area : map.getAreas()) {
                size += area.getPlayersByType(ConstTypeObject.TYPE_PLAYER).size();
            }
        }
        return size;
    }

    public String getNameMapHomeByGender(int gender) {
        String mapName = "Home";
        switch (gender) {
            case ConstPlayer.TRAI_DAT: {
                mapName = this.getNameMapById(ConstMap.NHA_GOHAN);
                break;
            }
            case ConstPlayer.NAMEC: {
                mapName = this.getNameMapById(ConstMap.NHA_MOORI);
                break;
            }
            case ConstPlayer.XAYDA: {
                mapName = this.getNameMapById(ConstMap.NHA_BROLY);
                break;
            }
        }
        return mapName;
    }

    public String getNameMapVillageByGender(int gender) {
        String mapName = "vilage";
        switch (gender) {
            case ConstPlayer.TRAI_DAT: {
                mapName = this.getNameMapById(ConstMap.LANG_ARU);
                break;
            }
            case ConstPlayer.NAMEC: {
                mapName = this.getNameMapById(ConstMap.LANG_MOORI);
                break;
            }
            case ConstPlayer.XAYDA: {
                mapName = this.getNameMapById(ConstMap.LANG_KAKAROT);
                break;
            }
        }
        return mapName;
    }

    public String getNameMapCliffByGender(int gender) {
        String mapName = "cliff";
        switch (gender) {
            case ConstPlayer.TRAI_DAT: {
                mapName = this.getNameMapById(ConstMap.VACH_NUI_ARU);
                break;
            }
            case ConstPlayer.NAMEC: {
                mapName = this.getNameMapById(ConstMap.VACH_NUI_MOORI);
                break;
            }
            case ConstPlayer.XAYDA: {
                mapName = this.getNameMapById(ConstMap.VAC_NUI_KAKAROT);
                break;
            }
        }
        return mapName;
    }

}
