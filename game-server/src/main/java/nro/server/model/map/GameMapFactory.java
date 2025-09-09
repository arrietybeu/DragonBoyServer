package nro.server.model.map;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nro.server.data_holders.repo.MapData;
import nro.server.engine.GameWorld;
import nro.server.data_holders.data.MonsterData;
import nro.server.model.map.zone.*;
import nro.server.model.map.zone.type.*;
import nro.server.model.npc.Npc;
import nro.server.model.npc.NpcFactory;
import nro.server.model.templates.entity.MonsterInfo;
import nro.server.model.templates.entity.MonsterTemplate;
import nro.server.model.templates.entity.NpcTemplate;
import nro.server.model.templates.world.WorldMapTemplate;
import nro.server.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            initMonster(gm, tpl);
            // log.info("Loaded map: {}", gm);
        }
        log.info("MapFactory: tổng cộng {} map đã được load", maps.size());
    }

    private void initMonster(GameMap gm, WorldMapTemplate tpl) {
        GameWorld.getInstance().submitToMain(() -> {
            spawnMonstersFromTemplate(gm, tpl.getMonster());
        });
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

        var mapID = t.getId();

        ZoneManager manager = switch (type) {
            case NORMAL -> new NormalZoneManager(mapID, t.getMaxArea(), t.getMaxPlayer());
            case OFFLINE -> new OfflineZoneManager(mapID);
            case DUNGEON -> throw new UnsupportedOperationException("DUNGEON chưa hỗ trợ cho map " + mapID);
            case EVENT -> throw new UnsupportedOperationException("EVENT chưa hỗ trợ cho map " + mapID);
        };

        return new GameMap(mapID, t.getName(), type, manager, getListNpcInGame(mapID, t.getNpcInfos()));
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

    private List<Npc> getListNpcInGame(int mapID, List<NpcTemplate.NpcInfo> npcInfos) {
        List<Npc> npcs = new ArrayList<>();
        for (var npcInfo : npcInfos) {
            var npc = NpcFactory.createNpc(npcInfo.template().id(), npcInfo.status(), mapID, npcInfo.x(), npcInfo.y(),
                    npcInfo.avatar());
            if (npc == null)
                continue;
            npcs.add(npc);
        }
        return npcs;
    }

    private void spawnMonstersFromTemplate(GameMap gm, List<MonsterInfo> infos) {
        if (infos == null || infos.isEmpty())
            return;

        short mapId = gm.id();

        gm.zoneManager().forEachZoneWrite(z -> {
            for (MonsterInfo monster : infos) {
                MonsterTemplate monsterTemplate = MonsterData.getInstance().getMonster(monster.templateID());
                MapUtils.spawnMonster(
                        mapId, z.zoneId(), monster.templateID(), monsterTemplate.NAME(),
                        monster.x(), monster.y(), monster.level(), monster.maxHp());
            }
        });
    }

    private static class Holder {
        private static final GameMapFactory INSTANCE = new GameMapFactory();
    }

    public static GameMapFactory getInstance() {
        return Holder.INSTANCE;
    }

}
