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

    //id, name, planetId, tileId, isMapDouble, type, bgId, bgType, bgItems, effects, waypoints, tileMap
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

    public boolean isVoDaiMap() {
        return this.id == 51
                || this.id == 103
                || this.id == 112
                || this.id == 113
                || this.id == 129
                || this.id == 130;
    }

    public boolean isTrainingMap() {
        return this.id == 39 || this.id == 40 || this.id == 41;
    }

    public boolean isMapLang() {
        return this.id == 0 || this.id == 7 || this.id == 14;
    }

    @Override
    public void run() {
    }
}
