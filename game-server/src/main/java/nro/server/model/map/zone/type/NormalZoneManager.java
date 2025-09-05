package nro.server.model.map.zone.type;

import nro.server.model.map.zone.BaseZone;
import nro.server.model.map.zone.Zone;
import nro.server.model.map.zone.ZoneManager;
import nro.server.model.map.zone.ZoneType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * @author Arriety
 */
public final class NormalZoneManager implements ZoneManager {

    private final short mapId;
    private final List<BaseZone> zones;
    private final AtomicIntegerArray counts;
    private final int maxPlayers;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
    public Zone joinZone(int playerId) {
        int pick = pickLeastLoaded();
        if (pick == -1) {
            // FIXME nếu đã đầy đảy về map offline (nhà,...)
            throw new IllegalStateException("Tất cả zone của map " + mapId + " đều đầy");
        }
        return zones.get(pick);
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

    /**
     * <p>Phù hợp cho tác vụ Lấy Dữ liệu trong Zone / GM tool / debug log
     *
     * @return {@code getZonesReadOnly}
     */
    public List<Zone> getZonesReadOnly() {
        lock.readLock().lock();
        try {
            return List.copyOf(zones);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * <pre><code>
     *      zoneManager.forEachZoneWrite(z -> {
     *           for (Player p : z.getPlayers()) {
     *              p.sendPacket(new Notify("Anh béo đẹp trai"));
     *           }
     *      });
     * </code></pre>
     *
     * @param consumer
     */
    public void forEachZoneWrite(Consumer<Zone> consumer) {
        lock.writeLock().lock();
        try {
            for (Zone z : zones) consumer.accept(z);
        } finally {
            lock.writeLock().unlock();
        }
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
