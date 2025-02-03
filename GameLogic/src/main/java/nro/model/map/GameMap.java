package nro.model.map;

import lombok.Data;
import nro.model.map.decorates.BackgroudEffect;
import nro.model.map.decorates.BgItem;

import java.util.List;

@Data
public class GameMap {

    private final int id;
    private final String name;
    private final byte planetId;
    private final byte tileId;
    private final byte bgId;
    private final byte typeMap;

    private final List<Waypoint> waypoints;
    private final List<BgItem> bgItems;
    private final List<BackgroudEffect> backgroudEffects;

    public GameMap(int id, String name, byte planetId, byte tileId, byte bgId, byte typeMap, List<BgItem> bgItems, List<BackgroudEffect> backgroudEffects, List<Waypoint> waypoints) {
        this.id = id;
        this.name = name;
        this.planetId = planetId;
        this.tileId = tileId;
        this.typeMap = typeMap;
        this.bgId = bgId;
        this.bgItems = bgItems;
        this.backgroudEffects = backgroudEffects;
        this.waypoints = waypoints;
    }

    public static boolean isVoDaiMap(int mapID) {
        return mapID == 51
                || mapID == 103
                || mapID == 112
                || mapID == 113
                || mapID == 129
                || mapID == 130;
    }
}
