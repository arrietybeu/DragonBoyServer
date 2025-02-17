package nro.model.map;

import lombok.Data;
import nro.model.map.areas.Area;
import nro.model.map.decorates.BackgroudEffect;
import nro.model.map.decorates.BgItem;
import nro.model.player.Player;
import nro.server.LogServer;
import nro.server.manager.MapManager;

import java.util.*;

@Data
public class GameMap implements Runnable {

    private static final Set<Integer> TILE_TOP_SET = new HashSet<>(Set.of(2, 3, 5, 7)); //vi tri co the dung duoc
    private static final int SIZE = 24;//  size của 1 cục đất

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
    private final NavigableMap<Integer, List<Waypoint>> waypointMap;
    private final List<BgItem> bgItems;
    private final List<BackgroudEffect> backgroudEffects;

    private List<Area> areas;

    //id, name, planetId, tileId, isMapDouble, type, bgId, bgType, bgItems, effects, waypoints, tileMap
    public GameMap(int id, String name, byte planetId, byte tileId, byte isMapDouble, byte bgId, byte bgType, byte typeMap, List<BgItem> bgItems, List<BackgroudEffect> backgroudEffects, List<Waypoint> waypoints, TileMap tileMap) {
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
        this.waypointMap = new TreeMap<>();
        for (Waypoint wp : waypoints) {
            waypointMap.computeIfAbsent((int) wp.getMinX(), k -> new ArrayList<>()).add(wp);
        }
    }

    public Waypoint getWayPointInMap(Player player) {
        try {
            var x = player.getX();
            var y = player.getY();

            if (this.id == 46) {
                int deltaX = 1000;
                NavigableMap<Integer, List<Waypoint>> subMap = waypointMap.subMap((int) x - deltaX, true, (int) x + deltaX, true);
                for (List<Waypoint> waypoints : subMap.values()) {
                    for (Waypoint wp : waypoints) {
                        if (x >= wp.getMinX() - deltaX && x <= wp.getMaxX() + deltaX &&
                                y >= wp.getMinY() && y <= wp.getMaxY()) {
                            return wp;
                        }
                    }
                }
            } else {
                Map.Entry<Integer, List<Waypoint>> entry = waypointMap.floorEntry((int) x);
                if (entry != null) {
                    for (Waypoint wp : entry.getValue()) {
                        if (x >= wp.getMinX() && x <= wp.getMaxX() && y >= wp.getMinY() && y <= wp.getMaxY()) {
                            return wp;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this.getWaypointBase(player);
    }

    private Waypoint getWaypointBase(Player player) {
        Waypoint waypoint = new Waypoint();
        waypoint.setGoMap(21 + player.getGender());
        waypoint.setGoX((short) 200);
        waypoint.setGoY((short) 336);
        return waypoint;
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
        return this.id == 51 || this.id == 103 || this.id == 112 || this.id == 113 || this.id == 129 || this.id == 130;
    }

    public int yPhysicInTop(int x, int y) {
        int rX = x / SIZE;
        int rY = 0;
        int row = y / SIZE;

        int tmw = this.tileMap.tmw();
        int[] tiles = this.tileMap.tiles();

        int index = row * tmw + rX;
        if (index >= 0 && index < tiles.length && isTileTop(tiles[index])) {
            return y;
        }

        for (int i = row; i < this.tileMap.tmh(); i++) {
            index = i * tmw + rX;
            if (index >= 0 && index < tiles.length && isTileTop(tiles[index])) {
                rY = i * SIZE;
                break;
            }
        }
        return rY;
    }

    private boolean isTileTop(int tile) {
        return TILE_TOP_SET.contains(tile);
    }

    public boolean isTrainingMap() {
        return this.id == 39 || this.id == 40 || this.id == 41;
    }

    public boolean isMapLang() {
        return this.id == 0 || this.id == 7 || this.id == 14;
    }

    @Override
    public void run() {
        while (MapManager.running) {
            try {
                long st = System.currentTimeMillis();
                for (Area area : this.areas) {
                    area.update();
                }
                long timeDo = System.currentTimeMillis() - st;
                Thread.sleep(1000 - timeDo);
            } catch (Exception ex) {
                LogServer.LogException("Error Update Map id: " + this.id + " name: " + this.name + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

}
