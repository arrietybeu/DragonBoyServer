package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.templates.world.WorldMapTemplate;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.utils.PacketSendUtility;
import nro.server.world.World;
import nro.server.world.WorldMap;

import java.io.IOException;

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
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        WorldMap map = World.getInstance().getMap(positionComponent.mapId);
        if (map == null) {
            throw new NullPointerException("Map not found for mapId: " + positionComponent.mapId);
        }
        var worldMapInstance = map.getWorldMapInstance(positionComponent.areaId);

        var mapTemplate = map.getTemplate();
        writeByte(map.getId());
        writeByte(mapTemplate.getPlanetId());
        writeByte(mapTemplate.getTileId());
        writeByte(mapTemplate.getBgId());
        writeByte(0);
        writeUTF(mapTemplate.getName());
        writeByte(positionComponent.areaId);

        PacketSendUtility.writeMapInfo(this, worldMapInstance, positionComponent);

        writeByte(mapTemplate.getIsMapDouble());
    }

    private void loadMapInfo(WorldMapTemplate mapTemplate) {
        writeShort(positionComponent.x);
        writeShort(positionComponent.y);

        var wayPoints = mapTemplate.getWaypoints();
        var bgItems = mapTemplate.getBgItems();
        var backgroundEffects = mapTemplate.getBackgroundEffects();

        // send waypoint
        writeByte(wayPoints.size());
        for (var wayPoint : wayPoints) {
            this.writeShort(wayPoint.getMinX());
            this.writeShort(wayPoint.getMinY());
            this.writeShort(wayPoint.getMaxX());
            this.writeShort(wayPoint.getMaxY());
            this.writeBoolean(wayPoint.isEnter());
            this.writeBoolean(wayPoint.isOffline());
            this.writeUTF(wayPoint.getName());
        }

        // send monster
        writeByte(0);
        writeByte(0);

        // send npc
        writeByte(0);

        // send itemMap
        writeByte(0);

        // send background map
        this.writeShort(bgItems.size());
        for (var bgItem : bgItems) {
            this.writeShort(bgItem.getId());
            this.writeShort(bgItem.getX());
            this.writeShort(bgItem.getY());
        }
        // send effect map
        this.writeShort(backgroundEffects.size());//write effect
        for (var backgroundEffect : backgroundEffects) {
            this.writeUTF(backgroundEffect.key());
            this.writeUTF(backgroundEffect.value());
        }

        writeByte(mapTemplate.getBgType());
        writeByte(positionComponent.teleport);

        positionComponent.teleport = 0;
    }
}
