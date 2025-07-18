package nro.server.world;

/**
 * @author Arriety
 */
public record WorldPosition(short mapId, int x, int y, int zoneId, WorldMapInstance parent) {
}
