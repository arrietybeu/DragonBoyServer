package nro.model.map;

import lombok.Data;
import nro.model.map.decorates.BackgroudEffect;
import nro.model.map.decorates.BgItem;

import java.util.List;

@Data
public class GameMap {

    private int id;
    private String name;
    private byte planetId;
    private byte tileId;
    private byte bgId;
    private byte typeMap;

    private final List<Waypoint> waypoints;
    private final List<BgItem> bgItems;
    private final List<BackgroudEffect> backgroudEffects;

    public GameMap(int id, String name, List<BgItem> bgItems, List<BackgroudEffect> backgroudEffects, List<Waypoint> waypoints) {
        this.id = id;
        this.name = name;
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
