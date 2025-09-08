package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.map.MapChangeType;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.services.ChangeMapService;
import nro.server.utils.MapUtils;

import java.util.Set;

@AClientPacketHandler(command = ConstsCmd.ZONE_CHANGE, validStates = {NroConnection.State.IN_GAME})
public class CmZoneChange extends NroClientPacket {

    private byte zoneID;

    public CmZoneChange(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        zoneID = readByte();
    }

    @Override
    protected void runImpl() {
        PositionComponent pos = getConnection().getEntity().getComponent(PositionComponent.class);

        var zone = MapUtils.findZone(pos.mapId, zoneID);

        if (zone == null)
            throw new RuntimeException("Request Zone " + zoneID + " not found");

        pos.setZoneTarget(zone.zoneId());
        log.debug("request suss zone ID: {}", zoneID);

        ChangeMapService.requestChangeMap(getClient().getEntity(), MapChangeType.ZONE, zone.mapId(), zone.zoneId(), pos.x, pos.y);
    }

}
