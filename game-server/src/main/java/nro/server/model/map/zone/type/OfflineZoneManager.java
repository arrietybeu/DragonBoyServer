package nro.server.model.map.zone.type;

import nro.server.model.map.zone.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Arriety
 */
public final class OfflineZoneManager implements ZoneManager, GC {

    private final short mapId;
    private final int maxPlayers = 1;

    private static final long TTL_MS = 10_000;

    private final ConcurrentHashMap<Integer, Entry> byOwner = new ConcurrentHashMap<>();

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
    public Zone joinNormalZone(int playerId, Integer targetZoneId, boolean autoSwitchIfFull) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Zone joinOfflineZone(int playerId) {
        return byOwner.compute(playerId, (k, v) -> {
            if (v == null) {
                BaseZone z = new BaseZone(mapId, playerId, ZoneType.OFFLINE, maxPlayers);
                return new Entry(z, -1);
            }
            return v;
        }).zone();
    }

    @Override
    public Zone joinDungeonZone(int guildId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Optional<Zone> findZone(int zoneId) {
        for (Entry e : byOwner.values()) if (e.zone.zoneId() == zoneId) return java.util.Optional.of(e.zone);
        return java.util.Optional.empty();
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
