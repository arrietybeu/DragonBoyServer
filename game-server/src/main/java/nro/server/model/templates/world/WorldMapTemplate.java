package nro.server.model.templates.world;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * @author Arriety
 */
@Getter
@Setter
public class WorldMapTemplate {

    private final short id;
    private int pixelWidth;
    private int pixelHeight;
    private final String name;

    private final byte planetId, tileId, bgId, bgType, typeMap, isMapDouble, maxArea, maxPlayer;

    private final TileMap tileMap;
    private final List<Waypoint> waypoints;
    private final NavigableMap<Integer, List<Waypoint>> waypointMap;
    private final List<BgItem> bgItems;
    private final List<BackgroundEffect> backgroundEffects;
    public int[] types;

    public WorldMapTemplate(int id, String name, byte maxArea, byte maxPlayer,
                            byte planetId, byte tileId,
                            byte isMapDouble, byte bgId, byte bgType,
                            byte typeMap, List<BgItem> bgItems,
                            List<BackgroundEffect> backgroundEffects,
                            List<Waypoint> waypoints, TileMap tileMap) {
        this.id = (short) id;
        this.name = name;
        this.maxArea = maxArea;
        this.maxPlayer = maxPlayer;
        this.planetId = planetId;
        this.tileId = tileId;
        this.isMapDouble = isMapDouble;
        this.bgId = bgId;
        this.bgType = bgType;
        this.typeMap = typeMap;
        this.bgItems = bgItems;
        this.backgroundEffects = backgroundEffects;
        this.waypoints = waypoints;
        this.tileMap = tileMap;
        this.waypointMap = new TreeMap<>();
        for (Waypoint wp : waypoints) {
            waypointMap.computeIfAbsent((int) wp.getMinX(), k -> new ArrayList<>()).add(wp);
        }
    }
}
