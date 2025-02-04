package nro.server.manager;

import lombok.Getter;
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
public class MapManager implements IManager {

    @Getter
    private static final MapManager instance = new MapManager();

    private final Map<Short, GameMap> gameMaps = new HashMap<>();
    private final List<BackgroundMapTemplate> backgroundMapTemplates = new ArrayList<>();
    private final List<TileSetTemplate> tileSetTemplates = new ArrayList<>();

    private byte[] BackgroundMapData;
    private byte[] TileSetData;

    @Override
    public void init() {
        this.loadMapTemplate();
        this.loadDataBackgroundMap();
        this.loadTileSetInfo();
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
        this.BackgroundMapData = null;
        this.TileSetData = null;
    }

    private void loadMapTemplate() {
        String query = "SELECT * FROM `map_template`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); var rs = ps.executeQuery()) {
            while (rs.next()) {
                short id = rs.getShort("id");
                var name = rs.getString("name");
                var zone = rs.getByte("zone");
                var maxPlayer = rs.getByte("max_player");
                var type = rs.getByte("type");
                var planetId = rs.getByte("planet_id");
                var tileId = rs.getByte("tile_id");
                var bgId = rs.getByte("background_id");
                var bgType = rs.getByte("background_type");

                List<BgItem> bgItems = this.loadItemBackgroundMap(connection, id);
                List<BackgroudEffect> effects = this.loadMapEffects(id);
                List<Waypoint> waypoints = this.loadWaypoints(connection, id);

                GameMap mapTemplate = new GameMap(id, name, planetId, tileId, type, bgId, bgItems, effects, waypoints);

                mapTemplate.setAreas(this.initArea(connection, mapTemplate, zone, maxPlayer));
                this.gameMaps.put(id, mapTemplate);
            }
            LogServer.LogInit("MapManager init size: " + this.gameMaps.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadMap: " + e.getMessage());
        }
    }

    private List<Area> initArea(Connection connection, GameMap map, int zone, int maxPlayer) {
        List<Area> areas = new ArrayList<>();
        for (int i = 0; i < zone; i++) {
            Area area = new Area(
                    map,
                    i,
                    maxPlayer,
                    this.loadMonsters(connection, map.getId()),
                    this.loadNpcs(connection, map.getId())
            );
            areas.add(area);
        }
        return areas;
    }

    private List<Monster> loadMonsters(Connection connection, int mapId) {
        List<Monster> monsters = new ArrayList<>();
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
                    monster.setBoss(rs.getBoolean("is_boss"));
                    monster.setDisable(rs.getBoolean("is_disable"));
                    monster.setDontMove(rs.getBoolean("is_dont_move"));
                    monster.setFire(rs.getBoolean("is_fire"));
                    monster.setIce(rs.getBoolean("is_ice"));
                    monster.setWind(rs.getBoolean("is_wind"));
                    monsters.add(monster);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadMonsters: " + e.getMessage());
        }
        return monsters;
    }

    private List<Npc> loadNpcs(Connection connection, int mapId) {
        List<Npc> npcs = new ArrayList<>();
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
                    waypoint.setEnter(rs.getBoolean("is_enter"));
                    waypoint.setOffline(rs.getBoolean("is_offline"));
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

    private List<BackgroudEffect> loadMapEffects(int mapId) {
        String queryBeff = "SELECT effect_id FROM `map_background_effect` WHERE map_id = ?";
        String queryEffect = "SELECT * FROM `map_effect` WHERE map_id = ?";
        String queryPosition = "SELECT * FROM `map_effect_position` WHERE effect_id = ?";

        List<BackgroudEffect> effects = new ArrayList<>();

//        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC)) {
//
//            try (PreparedStatement psBeff = connection.prepareStatement(queryBeff)) {
//                psBeff.setInt(1, mapId);
//                try (ResultSet rsBeff = psBeff.executeQuery()) {
//                    while (rsBeff.next()) {
//                        BackgroudEffect backgroundEffect = new BackgroudEffect();
//                        backgroundEffect.setKey("beff");
//                        backgroundEffect.setValue(String.valueOf(rsBeff.getInt("effect_id")));
//                        effects.add(backgroundEffect);
//                    }
//                }
//            }
//
//            try (PreparedStatement psEffect = connection.prepareStatement(queryEffect)) {
//                psEffect.setInt(1, mapId);
//                try (ResultSet rsEffect = psEffect.executeQuery()) {
//                    while (rsEffect.next()) {
//                        int effectId = rsEffect.getInt("id");
//                        int effectType = rsEffect.getInt("effect_id");
//                        int layer = rsEffect.getInt("layer");
//                        int loop = rsEffect.getInt("loop");
//                        int loopCount = rsEffect.getInt("loop_count");
//                        int typeEff = rsEffect.getInt("type_eff");
//                        int indexFrom = rsEffect.getInt("index_from");
//                        int indexTo = rsEffect.getInt("index_to");
//
//                        try (PreparedStatement psPosition = connection.prepareStatement(queryPosition)) {
//                            psPosition.setInt(1, effectId);
//                            try (ResultSet rsPosition = psPosition.executeQuery()) {
//                                while (rsPosition.next()) {
//                                    int x = rsPosition.getInt("x");
//                                    int y = rsPosition.getInt("y");
//
//                                    String value = effectType + "." + layer + "." + x + "." + y;
//                                    if (loop != -1 || loopCount != 1) {
//                                        value += "." + loop + "." + loopCount;
//                                    }
//                                    if (typeEff != 0) {
//                                        value += "." + typeEff;
//                                        if (indexFrom != 0 || indexTo != 0) {
//                                            value += "." + indexFrom + "." + indexTo;
//                                        }
//                                    }
//
//                                    BackgroudEffect effect = new BackgroudEffect();
//                                    effect.setKey("eff");
//                                    effect.setValue(value);
//                                    effects.add(effect);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch (SQLException e) {
//            LogServer.LogException("Error loadMapEffects: " + e.getMessage());
//        }

        return effects;
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
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); var rs = ps.executeQuery()) {
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

    public GameMap findMapById(short id) {
        if (this.gameMaps.isEmpty()) {
            return null;
        }
        if (id < 0 || id >= this.gameMaps.size()) {
            return null;
        }
        if (this.gameMaps.get(id).getId() != id) {
            return null;
        }
        return this.gameMaps.get(id);
    }

    public int sizeMap() {
        return this.gameMaps.size();
    }
}
