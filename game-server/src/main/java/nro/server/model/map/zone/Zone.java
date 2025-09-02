package nro.server.model.map.zone;

/**
 * @author Arriety
 */
public interface Zone {

    short mapId();

    int zoneId();

    String groupName();

    ZoneType type();

    int maxPlayers();

    int playerCount();

    void onPlayerJoin();

    void onPlayerLeave();
}
