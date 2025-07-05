package nro.server.world;

import nro.server.model.templates.world.WorldMapTemplate;

/**
 * @author Arriety
 */
public class WorldMap {
    private static final int SIZE = 24;

    private final WorldMapTemplate template;

    public WorldMap(WorldMapTemplate template) {
        this.template = template;
    }
}
