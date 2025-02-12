package nro.model.map;

import lombok.Data;
import nro.model.map.areas.Area;
import nro.model.map.decorates.BackgroudEffect;
import nro.model.map.decorates.BgItem;

import java.util.List;

@Data
public class GameMap implements Runnable {

    private final int id;
    private final String name;
    private final byte planetId;
    private final byte tileId;
    private final byte bgId;
    private final byte bgType;
    private final byte typeMap;
    private final byte isMapDouble;
    private final TileMap tileMap;

    private final List<Waypoint> waypoints;
    private final List<BgItem> bgItems;
    private final List<BackgroudEffect> backgroudEffects;

    private List<Area> areas;

    public GameMap(int id, String name, byte planetId,
                   byte tileId, byte isMapDouble, byte bgId, byte bgType, byte typeMap,
                   List<BgItem> bgItems, List<BackgroudEffect> backgroudEffects,
                   List<Waypoint> waypoints, TileMap tileMap) {
        this.id = id;
        this.name = name;
        this.planetId = planetId;
        this.tileId = tileId;
        this.isMapDouble = isMapDouble;
        this.bgId = bgId;
        this.bgType = bgType;
        this.typeMap = typeMap;
        this.bgItems = bgItems;
        this.backgroudEffects = backgroudEffects;
        this.waypoints = waypoints;
        this.tileMap = tileMap;
    }

    public Area getArea() {
        List<Area> areas = this.areas;
        for (Area area : areas) {
            int count = area.getPlayers().size();
            if (count < area.getMaxPlayers()) {
                return area;
            }
        }
        return null;
    }

    public static boolean isVoDaiMap(int mapID) {
        return mapID == 51
                || mapID == 103
                || mapID == 112
                || mapID == 113
                || mapID == 129
                || mapID == 130;
    }

    @Override
    public void run() {
    }
}
