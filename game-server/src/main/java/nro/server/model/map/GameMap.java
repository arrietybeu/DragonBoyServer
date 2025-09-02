package nro.server.model.map;

import nro.server.model.map.zone.ZoneManager;
import nro.server.model.map.zone.ZoneType;

/**
 * @author Arriety
 */
public record GameMap(short id, String name, ZoneType type, ZoneManager zoneManager) {
}
