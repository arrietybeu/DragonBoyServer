package nro.server.model.map.zone.type;

import nro.server.model.map.zone.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * @author Arriety
 */
public final class OfflineZoneManager implements ZoneManager, GC {

    private final short mapId;
    private final int maxPlayers = 1;
    private static final long TTL_MS = 10_000;

    private final ConcurrentHashMap<Integer, Entry> byOwner = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


    public OfflineZoneManager(short mapId) {
        this.mapId = mapId;
    }

    @Override
    public ZoneType type() {
        return ZoneType.OFFLINE;
    }

    @Override
    public int zoneCount() {
        return byOwner.size();
    }

    @Override
    public Zone joinZone(int entityID) {
        return byOwner.compute(entityID, (k, v) -> {
            if (v == null) {
                BaseZone z = new BaseZone(mapId, entityID, ZoneType.OFFLINE, maxPlayers);
                return new Entry(z, -1);
            }
            return v;
        }).zone();
    }

    @Override
    public Optional<Zone> findZone(int zoneId) {
        for (Entry e : byOwner.values()) if (e.zone.zoneId() == zoneId) return Optional.of(e.zone);
        return Optional.empty();
    }

    @Override
    public Collection<Zone> getZonesReadOnly() {
        lock.readLock().lock();
        try {
            return List.copyOf(byOwner.values().stream().map(Entry::zone).toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void forEachZoneWrite(Consumer<Zone> consumer) {
        lock.writeLock().lock();
        try {
            byOwner.forEach((id, e) -> consumer.accept(e.zone));
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void gc() {
        long now = System.currentTimeMillis();

        byOwner.replaceAll((id, e) -> {
            if (e.zone.playerCount() == 0) {
                long t = (e.lastEmptyAt == -1) ? now : e.lastEmptyAt;
                return new Entry(e.zone, t);
            }
            return new Entry(e.zone, -1);
        });
        byOwner.entrySet().removeIf(en -> {
            Entry e = en.getValue();
            return e.lastEmptyAt != -1 && now - e.lastEmptyAt >= TTL_MS;
        });
    }


    private record Entry(BaseZone zone, long lastEmptyAt) {
    }


}
