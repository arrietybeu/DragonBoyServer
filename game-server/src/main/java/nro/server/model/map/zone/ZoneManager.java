package nro.server.model.map.zone;

import java.util.Optional;

/**
 * @author Arriety
 */
public interface ZoneManager {

    ZoneType type();

    int zoneCount();

    // NORMAL
    Zone joinNormalZone(int playerId, Integer targetZoneId, boolean autoSwitchIfFull);

    // OFFLINE
    Zone joinOfflineZone(int playerId);

    // DUNGEON
    Zone joinDungeonZone(int guildId);

    Optional<Zone> findZone(int zoneId);

}
