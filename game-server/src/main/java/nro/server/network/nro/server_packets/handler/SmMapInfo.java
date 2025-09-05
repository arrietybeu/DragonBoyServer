package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.MapData;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.utils.MapUtils;

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
        var mapTemplate = MapData.getInstance().getWorldMapTemplate(positionComponent.mapId);

        writeByte(positionComponent.mapId);
        writeByte(mapTemplate.getPlanetId());
        writeByte(mapTemplate.getTileId());
        writeByte(mapTemplate.getBgId());
        writeByte(0);
        writeUTF(mapTemplate.getName());
        writeByte(positionComponent.getAreaId());

        PacketHelper.writeMapInfo(this, mapTemplate, positionComponent);

        writeByte(mapTemplate.getIsMapDouble());
    }

}
