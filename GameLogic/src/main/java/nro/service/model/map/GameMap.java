package nro.service.model.map;

import lombok.Getter;
import lombok.Setter;
import nro.consts.ConstMap;
import nro.consts.ConstTypeObject;
import nro.service.model.map.areas.Area;
import nro.service.model.map.decorates.BackgroudEffect;
import nro.service.model.map.decorates.BgItem;
import nro.service.model.npc.Npc;
import nro.service.model.npc.NpcFactory;
import nro.service.model.entity.player.Player;
import nro.service.model.template.NpcTemplate;
import nro.server.system.LogServer;
import nro.server.manager.MapManager;

import java.util.*;
import java.util.concurrent.atomic.AtomicIntegerArray;

@Getter
@Setter
public class GameMap implements Runnable {

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
    private final List<BackgroudEffect> backgroudEffects;
    private final List<NpcTemplate.NpcInfo> npcs;

    private List<Area> areas;

    // id, name, planetId, tileId, isMapDouble, status, bgId, bgType, bgItems,
    // effects, waypoints, tileMap
    public GameMap(int id, String name, byte planetId, byte tileId, byte isMapDouble, byte bgId, byte bgType,
                   byte typeMap,
                   List<BgItem> bgItems, List<BackgroudEffect> backgroudEffects, List<Waypoint> waypoints, TileMap tileMap,
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
        this.backgroudEffects = backgroudEffects;
        this.waypoints = waypoints;
        this.tileMap = tileMap;
        this.waypointMap = new TreeMap<>();
        this.npcs = npcs;
        for (Waypoint wp : waypoints) {
            waypointMap.computeIfAbsent((int) wp.getMinX(), k -> new ArrayList<>()).add(wp);
        }
    }

    public void initNpc() {
        for (Area area : this.areas) {
            for (var npc : this.npcs) {
                Npc npcArea = NpcFactory.createNpc(npc.npcId(), npc.status(), this.id, npc.x(), npc.y(), npc.avatar());
                if (npcArea == null)
                    continue;
                area.getNpcList().add(npcArea);
            }
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
            if (area.getPlayersByType(ConstTypeObject.TYPE_PLAYER).size() < area.getMaxPlayers()) {
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

    public int tileTypeAt(int x, int y) {
        try {
            AtomicIntegerArray types = new AtomicIntegerArray(new int[this.tileMap.tiles().length]);
            return types.get(y * this.tileMap.tmw() + x);
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
                LogServer.LogException("Error Update Map id: " + this.id + " name: "
                        + this.name + ex.getMessage(), ex);
            }
        }
    }

}
