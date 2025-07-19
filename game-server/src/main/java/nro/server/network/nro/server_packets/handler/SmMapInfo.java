package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.engine.GameWorld;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.templates.world.WorldMapTemplate;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.world.World;
import nro.server.world.WorldMap;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.MAP_INFO)
public class SmMapInfo extends NroServerPacket {

    private final PositionComponent positionComponent;

    public SmMapInfo(final PositionComponent positionComponent) {
        this.positionComponent = positionComponent;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        WorldMap map = World.getInstance().getMap(positionComponent.mapId);
        var mapTemplate = map.getTemplate();
        writeByte(map.getId());
        writeByte(mapTemplate.getPlanetId());
        writeByte(mapTemplate.getTileId());
        writeByte(mapTemplate.getBgId());
        writeByte(0);
        writeUTF(mapTemplate.getName());
        writeByte(positionComponent.areaId);
        this.loadMapInfo(mapTemplate);
        writeByte(mapTemplate.getIsMapDouble());
    }

    private void loadMapInfo(WorldMapTemplate mapTemplate) {
        writeShort(positionComponent.x);
        writeShort(positionComponent.y);
        // send waypoint
        writeByte(0);
        // send monster
        writeByte(0);
        writeByte(0);

        // send npc
        writeByte(0);

        // send itemMap
        writeByte(0);

        // send background map
        writeShort(0);
        // send effect map
        writeShort(0);

        writeByte(mapTemplate.getBgType());
        writeByte(positionComponent.teleport);

        positionComponent.teleport = 0;
    }
}
