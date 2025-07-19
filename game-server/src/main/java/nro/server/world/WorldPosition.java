package nro.server.world;

/**
 * @author Arriety
 */
public record WorldPosition(short mapId, short x, short y, int zoneId, WorldMapInstance parent) {
}
