package nro.model.map;

import lombok.Data;
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

    private List<Waypoint> waypoints;
    private List<BgItem> bgItems;

    public GameMap(int id, String name) {
        this.id = id;
        this.name = name;
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
