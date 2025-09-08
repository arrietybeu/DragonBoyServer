package nro.server.model.templates.world;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nro.server.consts.ConstMap;
import nro.server.data_holders.data.MapData;
import nro.server.model.templates.entity.MonsterInfo;
import nro.server.model.templates.entity.NpcTemplate;

import java.util.*;

/**
 * @author Arriety
 */
@Slf4j
@Getter
@Setter
public class WorldMapTemplate {

    private final short id;
    private int pixelWidth;
    private int pixelHeight;
    private final String name;

    private static final int SIZE = 24;// size của 1 cục đất (giá trị kích thước một ô tile) 24 x 24

    private final byte planetId, tileId, bgId, bgType, typeMap, isMapDouble, maxArea, maxPlayer;

    private final TileMap tileMap;
    private final List<Waypoint> waypoints;
    private final NavigableMap<Integer, List<Waypoint>> waypointMap;
    private final List<BgItem> bgItems;
    private final List<BackgroundEffect> backgroundEffects;

    private final List<NpcTemplate.NpcInfo> npcInfos;
    private final List<MonsterInfo> monster;

    public int[] types;

    public WorldMapTemplate(int id, String name, byte maxArea, byte maxPlayer,
            byte planetId, byte tileId,
            byte isMapDouble, byte bgId, byte bgType,
            byte typeMap, List<BgItem> bgItems,
            List<BackgroundEffect> backgroundEffects,
            List<Waypoint> waypoints, TileMap tileMap, List<NpcTemplate.NpcInfo> npcInfos, List<MonsterInfo> monster) {
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
        this.npcInfos = npcInfos;
        this.monster = monster;
        loadTileMap(tileId);
    }

    public void loadTileMap(int tileId) {
        this.types = new int[tileMap.tiles().length];
        pixelHeight = tileMap.height() * SIZE;
        pixelWidth = tileMap.width() * SIZE;
        int num = tileId - 1;

        try {
            int[] tiles = tileMap.tiles();
            MapData mapManager = MapData.getInstance();
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
            log.warn("Error loading tile map for tileId {} in map {}", tileId, id, ex);
        }
    }

    public int tileTypeAtPixel(int px, int py) {
        int x = px / SIZE, y = py / SIZE;
        if (x < 0 || y < 0 || x >= tileMap.width() || y >= tileMap.height())
            return 1000;
        int index = y * tileMap.width() + x;
        return (index < 0 || index >= types.length) ? 1000 : types[index];
    }

    public short touchY(int px, int py) {
        int tx = px / SIZE;
        int y = py;

        int width = tileMap.width();
        int height = tileMap.height();

        if (tx < 0 || tx >= width)
            return (short) this.pixelHeight;

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

    public boolean isTouchY(int x, int y) {
        int tx = x / SIZE;
        int ty = y / SIZE;

        int width = tileMap.width();
        int height = tileMap.height();

        if (tx < 0 || tx >= width)
            return false;

        for (int j = ty; j < height; j++) {
            int index = j * width + tx;
            if ((types[index] & ConstMap.T_TOP) != 0) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "WorldMapTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxArea=" + maxArea +
                ", maxPlayer=" + maxPlayer +
                ", planetId=" + planetId +
                ", tileId=" + tileId +
                ", isMapDouble=" + isMapDouble +
                ", bgId=" + bgId +
                ", bgType=" + bgType +
                ", typeMap=" + typeMap +
                ", pixelWidth=" + pixelWidth +
                ", pixelHeight=" + pixelHeight +
                ", tileMap=" + tileMap +
                ", waypoints=" + waypoints.size() +
                ", bgItems=" + bgItems.size() +
                ", backgroundEffects=" + backgroundEffects.size() +
                ", npcInfos=" + npcInfos.size() +
                '}';
    }
}
