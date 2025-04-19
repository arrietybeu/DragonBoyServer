package nro.server.service.model.map;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstMap;
import nro.consts.ConstTypeObject;
import nro.server.manager.MapManager;
import nro.server.service.model.entity.Entity;
import nro.server.service.model.entity.ai.boss.Boss;
import nro.server.service.model.map.areas.Area;
import nro.server.service.model.map.decorates.BackgroudEffect;
import nro.server.service.model.map.decorates.BgItem;
import nro.server.service.model.entity.player.Player;
import nro.server.service.model.template.NpcTemplate;
import nro.server.system.LogServer;

import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Getter
@Setter
public class GameMap {

    private static final Set<Integer> TILE_TOP_SET = new HashSet<>(Set.of(2, 3, 5, 7)); // vi tri co the dung duoc

    private static final int SIZE = 24;// size của 1 cục đất (giá trị kích thước một ô tile) 24 x 24

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
    private final List<BackgroudEffect> backgroundEffects;
    private final List<NpcTemplate.NpcInfo> npcs;

    private List<Area> areas;

    // id, name, planetId, tileId, isMapDouble, status, bgId, bgType, bgItems,
    // effects, waypoints, tileMap
    public GameMap(int id, String name, byte planetId, byte tileId, byte isMapDouble,
                   byte bgId, byte bgType, byte typeMap,
                   List<BgItem> bgItems, List<BackgroudEffect> backgroundEffects,
                   List<Waypoint> waypoints, TileMap tileMap,
                   List<NpcTemplate.NpcInfo> npcs) {
        this.id = id;
        this.name = name;
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
        this.npcs = npcs;
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
                NavigableMap<Integer, List<Waypoint>> subMap = waypointMap.subMap((int) x - deltaX, true,
                        (int) x + deltaX, true);
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
            LogServer.LogException("Error getWayPointInMap id: " + this.id + " name: " + this.name + ex.getMessage(), ex);
        }

//        return this.getWaypointBase(player);
        return null;
    }

    public Waypoint getWaypointByGoMap(int idMapGo) {
        for (Waypoint waypoint : this.waypoints) {
            if (waypoint.getGoMap() == idMapGo) {
                return waypoint;
            }
        }
        return null;
    }

    private Waypoint getWaypointBase(Player player) {
        Waypoint waypoint = new Waypoint();
        waypoint.setGoMap(21 + player.getGender());
        waypoint.setGoX((short) 200);
        waypoint.setGoY((short) 336);
        return waypoint;
    }

    public Area getArea(int id, Entity entity) {
        try {
            for (Area area : this.areas) {
                if (entity instanceof Player player) {
                    if (area.getEntitysByType(ConstTypeObject.TYPE_PLAYER).size() < area.getMaxPlayers() &&
                            (id < 0 || area.getId() == id)) {
                        if (area.getMap().typeMap == ConstMap.MAP_OFFLINE) {
                            MapManager mapManager = MapManager.getInstance();
                            mapManager.createOfflineArea(player, area);
                            return mapManager.getOfflineArea(player);
                        }
                        return area;
                    }
                } else if (entity instanceof Boss) {
                    if (id < 0 || area.getId() == id) {
                        return area;
                    }
                } else {
                    LogServer.LogException("Not support entity: " + entity.getClass().getSimpleName());
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("Error getArea: " + ex.getMessage(), ex);
        }
        return null;
    }

    public boolean isVoDaiMap() {
        return this.id == 51 || this.id == 103 || this.id == 112 || this.id == 113 || this.id == 129 || this.id == 130;
    }

    public int getGroundY(int tileX) {
        TileMap tileMap = this.tileMap;
        for (int y = 0; y < tileMap.height(); y++) {
            int index = y * tileMap.width() + tileX;
            if (tileMap.tiles()[index] != 0) {
                return y;
            }
        }
        return -1;
    }


    public int tileTypeAt(int x, int y) {
        try {
            AtomicIntegerArray types = new AtomicIntegerArray(new int[this.tileMap.tiles().length]);
            return types.get(y * this.tileMap.width() + x);
        } catch (Exception exception) {
            return 1000;
        }
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

    public boolean isMapHouseByGender(int gender) {
        return gender == 0 && this.id == 21 || gender == 1 && this.id == 22 || gender == 2 && this.id == 23;
    }

    public boolean isMapOffline() {
        return this.typeMap == ConstMap.MAP_OFFLINE;
    }


}
