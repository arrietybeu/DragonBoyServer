package nro.server.dao;

import nro.commons.database.DatabaseFactory;
import nro.commons.database.DatabaseType;
import nro.server.model.templates.world.TileMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Arriety
 */
public class MapDAO {

    private final static String QUERY_LOAD_MAP_TEMPLATE = "SELECT * FROM `map_template`";

    private static void loadMapTemplate() {
        try (Connection con = DatabaseFactory.getConnection(DatabaseType.STATIC);
             PreparedStatement stmt = con.prepareStatement(QUERY_LOAD_MAP_TEMPLATE);
             var rs = stmt.executeQuery()) {

            Map<Integer, TileMap> tileMaps = loadAllMapTiles(con);

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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<Integer, TileMap> loadAllMapTiles(Connection connection) throws SQLException {
        return null;
    }

}
