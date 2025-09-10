package nro.server.model.map;

import nro.server.model.map.zone.ZoneManager;
import nro.server.model.map.zone.ZoneType;
import nro.server.model.npc.Npc;

import java.util.List;

/**
 * @author Arriety
 */
public record GameMap(
        short id,
        String name,
        ZoneType type,
        ZoneManager zoneManager,
        List<Npc> npcs) {
}
