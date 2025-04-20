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

    private static final int SIZE = 24;// size của 1 cục đất (giá trị kích thước một ô tile) 24 x 24

    private final int id;
    private int pixelWidth;
    private int pixelHeight;

    private final String name;

    private final byte planetId, tileId, bgId, bgType, typeMap, isMapDouble;

    private final TileMap tileMap;
    private final List<Waypoint> waypoints;
    private final NavigableMap<Integer, List<Waypoint>> waypointMap;
    private final List<BgItem> bgItems;
    private final List<BackgroudEffect> backgroundEffects;
    private final List<NpcTemplate.NpcInfo> npcs;
    private List<Area> areas;
    public int[] types;

    // id, name, planetId, tileId, isMapDouble, status, bgId, bgType, bgItems,
    // effects, waypoints, tileMap
    public GameMap(int id, String name, byte planetId, byte tileId, byte isMapDouble,
                   byte bgId, byte bgType, byte typeMap,
                   List<BgItem> bgItems, List<BackgroudEffect> backgroundEffects,
                   List<Waypoint> waypoints, TileMap tileMap,
                   List<NpcTemplate.NpcInfo> npcInfos) {
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
        this.npcs = npcInfos;
        for (Waypoint wp : waypoints) {
            waypointMap.computeIfAbsent((int) wp.getMinX(), k -> new ArrayList<>()).add(wp);
        }
    }

    public void loadTileMap(int tileId) {
        this.types = new int[tileMap.tiles().length];
        pixelHeight = tileMap.height() * SIZE;
        pixelWidth = tileMap.width() * SIZE;
        int num = tileId - 1;

        try {
            int[] tiles = tileMap.tiles();
            MapManager mapManager = MapManager.getInstance();
            int[][] indexList = mapManager.tileIndex[num];
            int[] typeList = mapManager.tileType[num];

            for (int i = 0; i < tiles.length; i++) {
                int tile = tiles[i];
                for (int j = 0; j < indexList.length; j++) {
                    for (int indexVal : indexList[j]) {
                        if (tile == indexVal) {
                            types[i] |= typeList[j];
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LogServer.LogException("loadTileMap: [" + tileId + "] " + ex.getMessage(), ex);
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

    public int tileTypeAtPixel(int px, int py) {
        int x = px / SIZE, y = py / SIZE;
        if (x < 0 || y < 0 || x >= tileMap.width() || y >= tileMap.height()) return 1000;
        int index = y * tileMap.width() + x;
        return (index < 0 || index >= types.length) ? 1000 : types[index];
    }

    public short touchY(int px, int py) {
        int tx = px / SIZE;
        int y = py;

        int width = tileMap.width();
        int height = tileMap.height();

        if (tx < 0 || tx >= width) return (short) this.pixelHeight;

        while (y < this.pixelHeight) {
            int ty = y / SIZE;
            int index = ty * width + tx;
            if ((types[index] & ConstMap.T_TOP) != 0) {
                return (short) (ty * SIZE);
            }
            y++;
        }

        return (short) this.pixelHeight;
    }


    public boolean isPlayerOnGround(int x, int y) {
        return (tileTypeAtPixel(x, y + 1) & ConstMap.T_TOP) != 0;
    }

    public boolean isVoDaiMap() {
        return switch (id) {
            case 51, 103, 112, 113, 129, 130 -> true;
            default -> false;
        };
    }

    public boolean isTrainingMap() {
        return switch (id) {
            case 39, 40, 41 -> true;
            default -> false;
        };
    }

    public boolean isMapLang() {
        return switch (id) {
            case 0, 7, 14 -> true;
            default -> false;
        };
    }

    public boolean isTouchY(int x, int y) {
        int tx = x / SIZE;
        int ty = y / SIZE;

        int width = tileMap.width();
        int height = tileMap.height();

        if (tx < 0 || tx >= width) return false;

        for (int j = ty; j < height; j++) {
            int index = j * width + tx;
            if ((types[index] & ConstMap.T_TOP) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isMapHouseByGender(int gender) {
        return switch (gender) {
            case 0 -> id == 21;
            case 1 -> id == 22;
            case 2 -> id == 23;
            default -> false;
        };
    }

    public boolean isMapOffline() {
        return this.typeMap == ConstMap.MAP_OFFLINE;
    }

    @Override
    public String toString() {
        return "Map " + id + " - " + name;
    }
}
