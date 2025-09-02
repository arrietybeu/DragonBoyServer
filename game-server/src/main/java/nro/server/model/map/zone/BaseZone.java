package nro.server.model.map.zone;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Arriety
 */
public final class BaseZone implements Zone {

    private final short mapId;
    private final int zoneId;
    private final ZoneType type;
    private final int maxPlayers;
    private final String groupName;

    private final AtomicInteger players = new AtomicInteger();

    public BaseZone(short mapId, int zoneId, ZoneType type, int maxPlayers) {
        this.mapId = mapId;
        this.zoneId = zoneId;
        this.type = type;
        this.maxPlayers = maxPlayers;
        this.groupName = "map_" + mapId + "_zone_" + zoneId;
    }

    @Override
    public short mapId() {
        return mapId;
    }

    @Override
    public int zoneId() {
        return zoneId;
    }

    @Override
    public String groupName() {
        return groupName;
    }

    @Override
    public ZoneType type() {
        return type;
    }

    @Override
    public int maxPlayers() {
        return maxPlayers;
    }

    @Override
    public int playerCount() {
        return players.get();
    }

    @Override
    public void onPlayerJoin() {
        players.incrementAndGet();
    }

    @Override
    public void onPlayerLeave() {
        players.decrementAndGet();
    }
}
