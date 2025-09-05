package nro.server.model.map.zone;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Arriety
 */
public interface ZoneManager {

    ZoneType type();

    int zoneCount();

    Zone joinZone(int playerId);

    Optional<Zone> findZone(int zoneId);

    Collection<Zone> getZonesReadOnly();

    void forEachZoneWrite(Consumer<Zone> consumer);

}
