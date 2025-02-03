package nro.server.manager;

import lombok.Getter;
import nro.model.map.decorates.BackgroudEffect;
import nro.model.map.decorates.BgItem;
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
import java.util.List;

@Getter
public class MapManager implements IManager {

    private static MapManager instance;

    private final List<GameMap> gameMaps = new ArrayList<>();
    private final List<BackgroundMapTemplate> backgroundMapTemplates = new ArrayList<>();
    private final List<TileSetTemplate> tileSetTemplates = new ArrayList<>();

    private byte[] BackgroundMapData;

    public static MapManager getInstance() {
        if (instance == null) {
            instance = new MapManager();
        }
        return instance;
    }

    @Override
    public void init() {
        this.clear();
        this.loadMapTemplate();
        this.loadDataBackgroundMap();
        this.loadTileSetInfo();
    }

    @Override
    public void reload() {
    }

    @Override
    public void clear() {
        this.gameMaps.clear();
    }

    private void loadMapTemplate() {
        String query = "SELECT * FROM `map_template`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC); PreparedStatement ps = connection.prepareStatement(query); var rs = ps.executeQuery()) {
            while (rs.next()) {
                var id = rs.getInt("id");
                var name = rs.getString("name");
                var mapTemplate = new GameMap(id, name);
                mapTemplate.setBgItems(this.loadItemBackgroundMap(id));
                this.gameMaps.add(mapTemplate);
            }
            LogServer.LogInit("MapManager init size: " + this.gameMaps.size());
        } catch (Exception e) {
            LogServer.LogException("Error loadMap: " + e.getMessage());
        }
    }

    private List<BgItem> loadItemBackgroundMap(int id) {
        String query = "SELECT * FROM `map_item_background` WHERE map_id = ?";
        List<BgItem> bgItems = new ArrayList<>();
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(query)) {
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

    @SuppressWarnings("ALL")
    private List<BackgroudEffect> loadMapEffects(int mapId) {
        String queryEffect = "SELECT * FROM `map_effect` WHERE map_id = ?";
        String queryPosition = "SELECT * FROM `map_effect_position` WHERE effect_id = ?";

        List<BackgroudEffect> effects = new ArrayList<>();

        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement psEffect = connection.prepareStatement(queryEffect)) {

            psEffect.setInt(1, mapId);
            try (ResultSet rsEffect = psEffect.executeQuery()) {
                while (rsEffect.next()) {
                    int effectId = rsEffect.getInt("id");
                    int effectType = rsEffect.getInt("effect_id");
                    int loop = rsEffect.getInt("loop");
                    int loopCount = rsEffect.getInt("loop_count");
                    int typeEff = rsEffect.getInt("type_eff");
                    int indexFrom = rsEffect.getInt("index_from");
                    int indexTo = rsEffect.getInt("index_to");

                    JSONArray positions = new JSONArray();
                    try (PreparedStatement psPosition = connection.prepareStatement(queryPosition)) {
                        psPosition.setInt(1, effectId);
                        try (ResultSet rsPosition = psPosition.executeQuery()) {
                            while (rsPosition.next()) {
                                int x = rsPosition.getInt("x");
                                int y = rsPosition.getInt("y");
                                int layer = rsPosition.getInt("layer");

                                JSONArray pos = new JSONArray();
                                pos.add(x);
                                pos.add(y);
                                pos.add(layer);
                                positions.add(pos);
                            }
                        }
                    }

                    String value;
                    if (positions.isEmpty()) {
                        value = effectType + "." + loop + "." + loopCount + "." + typeEff + "." + indexFrom + "." + indexTo;
                    } else {
                        value = effectType + "." + positions.toJSONString() + "." + loop + "." + loopCount + "." + typeEff + "." + indexFrom + "." + indexTo;
                    }

                    BackgroudEffect effect = new BackgroudEffect();
                    effect.setKey("eff");
                    effect.setValue(value);
                    effects.add(effect);
                }
            }
        } catch (SQLException e) {
            LogServer.LogException("Error loadMapEffects: " + e.getMessage());
        }

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

    private void loadTileSetInfo() {
        String query = "SELECT * FROM `map_tile_set_info`";
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(query);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                var tileSet = new TileSetTemplate();
                tileSet.setId(rs.getInt("id"));
                tileSet.setTile_type(rs.getByte("tile_type"));
                var tileTypes = this.loadTileType(tileSet.getId());
                tileSet.setTileTypes(tileTypes);
                this.tileSetTemplates.add(tileSet);
            }
            LogServer.LogInit("LoadTileSetInfo initialized size: " + this.tileSetTemplates.size());
        } catch (SQLException e) {
            LogServer.LogException("Error loadTileSetInfo: " + e.getMessage());
        }
    }

    private List<TileSetTemplate.TileType> loadTileType(int tileSetId) {
        String query = "SELECT * FROM `map_tile_type` WHERE tile_set_id = ?";
        List<TileSetTemplate.TileType> tileTypes = new ArrayList<>();
        try (Connection connection = DatabaseConnectionPool.getConnectionForTask(ConfigDB.DATABASE_STATIC);
             PreparedStatement ps = connection.prepareStatement(query)) {
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
                    dataOutputStream.writeShort(0);
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

    public GameMap findMapById(int id) {
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
