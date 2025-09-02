package nro.server.model.map.zone.type;

import nro.server.model.map.zone.BaseZone;
import nro.server.model.map.zone.Zone;
import nro.server.model.map.zone.ZoneManager;
import nro.server.model.map.zone.ZoneType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * @author Arriety
 */
public final class NormalZoneManager implements ZoneManager {

    private final short mapId;
    private final List<BaseZone> zones;
    private final AtomicIntegerArray counts;
    private final int maxPlayers;

    public NormalZoneManager(short mapId, int maxArea, int maxPlayers) {
        if (maxArea <= 0) throw new IllegalArgumentException("maxArea phải > 0");
        if (maxPlayers <= 0) throw new IllegalArgumentException("maxPlayers phải > 0");
        this.mapId = mapId;
        this.maxPlayers = maxPlayers;
        this.zones = new ArrayList<>(maxArea);
        this.counts = new AtomicIntegerArray(maxArea);

        for (int i = 0; i < maxArea; i++) {
            zones.add(new BaseZone(mapId, i, ZoneType.NORMAL, maxPlayers));
        }
    }

    @Override
    public Zone joinNormalZone(int playerId, Integer targetZoneId, boolean autoSwitchIfFull) {
        if (targetZoneId != null) {
            int z = targetZoneId;
            if (valid(z) && counts.get(z) < maxPlayers) {
                return zones.get(z);
            }
            if (!autoSwitchIfFull) {
                throw new IllegalStateException("Zone " + z + " đã đầy hoặc không tồn tại");
            }
        }
        int pick = pickLeastLoaded();
        if (pick == -1) {
            // FIXME nếu đã đầy đảy về map offline (nhà,...)
            throw new IllegalStateException("Tất cả zone của map " + mapId + " đều đầy");
        }
        return zones.get(pick);
    }

    @Override
    public Zone joinOfflineZone(int playerId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Zone joinDungeonZone(int guildId) {
        throw new UnsupportedOperationException();
    }

    public void onPlayerJoin(Zone z) {
        int id = z.zoneId();
        if (valid(id)) counts.incrementAndGet(id);
    }

    public void onPlayerLeave(Zone z) {
        int id = z.zoneId();
        if (valid(id)) counts.decrementAndGet(id);
    }

    @Override
    public Optional<Zone> findZone(int zoneId) {
        return valid(zoneId) ? Optional.of(zones.get(zoneId)) : Optional.empty();
    }

    private boolean valid(int zoneId) {
        return zoneId >= 0 && zoneId < zones.size();
    }

    private int pickLeastLoaded() {
        int best = -1, load = Integer.MAX_VALUE;
        for (int i = 0; i < zones.size(); i++) {
            int c = counts.get(i);
            if (c < maxPlayers && c < load) {
                load = c;
                best = i;
            }
        }
        return best;
    }

    @Override
    public ZoneType type() {
        return ZoneType.NORMAL;
    }

    @Override
    public int zoneCount() {
        return zones.size();
    }

}
