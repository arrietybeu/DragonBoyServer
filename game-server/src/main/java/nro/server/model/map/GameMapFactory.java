package nro.server.model.map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nro.server.data_holders.data.MapData;
import nro.server.model.map.zone.*;
import nro.server.model.map.zone.type.*;
import nro.server.model.templates.world.WorldMapTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arriety
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameMapFactory {

    private static final Logger log = LoggerFactory.getLogger(GameMapFactory.class);

    private final Map<Short, GameMap> maps = new HashMap<>();

    public void initMaps() {
        for (WorldMapTemplate tpl : MapData.getInstance().getWorldMaps().values()) {
            GameMap gm = this.fromTemplate(tpl);
            addMap(gm);
            log.info("Loaded map: {}", gm);
        }
        log.info("MapFactory: tổng cộng {} map đã được load", maps.size());
    }

    public void addMap(GameMap map) {
        maps.put(map.id(), map);
    }

    public GameMap getMap(short id) {
        return maps.get(id);
    }

    public Collection<GameMap> getAllMaps() {
        return maps.values();
    }

    private GameMap fromTemplate(WorldMapTemplate t) {
        ZoneType type = getZoneType(t);

        ZoneManager manager = switch (type) {
            case NORMAL -> new NormalZoneManager(t.getId(), t.getMaxArea(), t.getMaxPlayer());
            case OFFLINE -> new OfflineZoneManager(t.getId());
            case DUNGEON -> throw new UnsupportedOperationException("DUNGEON chưa hỗ trợ cho map " + t.getId());
            case EVENT -> throw new UnsupportedOperationException("EVENT chưa hỗ trợ cho map " + t.getId());
        };

        return new GameMap(t.getId(), t.getName(), type, manager);
    }

    private static ZoneType getZoneType(WorldMapTemplate t) {
        ZoneType type = switch (t.getTypeMap()) {
            case 0 -> ZoneType.NORMAL;
            case 1 -> ZoneType.OFFLINE;
            case 2 -> ZoneType.DUNGEON;
            case 3 -> ZoneType.EVENT;
            default -> throw new IllegalArgumentException("Unknown typeMap " + t.getTypeMap());
        };

        if (type == ZoneType.NORMAL) {
            if (t.getMaxArea() <= 0) {
                throw new IllegalArgumentException("Map " + t.getId() + " NORMAL nhưng maxArea <= 0");
            }
            if (t.getMaxPlayer() <= 0) {
                throw new IllegalArgumentException("Map " + t.getId() + " NORMAL nhưng maxPlayer <= 0");
            }
        }
        return type;
    }


    private static class Holder {
        private static final GameMapFactory INSTANCE = new GameMapFactory();
    }

    public static GameMapFactory getInstance() {
        return Holder.INSTANCE;
    }

}
