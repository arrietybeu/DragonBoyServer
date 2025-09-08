package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.MapData;
import nro.server.model.ecs.component.PositionComponent;
import nro.server.model.map.zone.ZoneType;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmOpenUiZone;
import nro.server.services.NotifyService;
import nro.server.services.NotifyType;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.OPEN_UI_ZONE, validStates = {NroConnection.State.IN_GAME})
public class CmOpenUIZone extends NroClientPacket {

    public CmOpenUIZone(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        // nothing to read

        // TODO có thể check kỹ thêm laf entity co active khong co online khong
    }

    @Override
    protected void runImpl() {

        final var pos = getConnection().getEntity().getComponent(PositionComponent.class);

        if (pos == null) {
            throw new RuntimeException("Position is null for client: " + getConnection());
        }

        var mapTemplate = MapData.getInstance().getWorldMapTemplate(pos.mapId);

        if (mapTemplate.getTypeMap() == ZoneType.OFFLINE.getValue()) {
            if (getConnection().getAccount().isAdmin()) {
                NotifyService.SendNotifyPlayer(getConnection(), NotifyType.FLYING_CAT, "Do bạn là Admin nên được phép dùng chức năng này!");
            } else {
                NotifyService.SendNotifyPlayer(getConnection(), NotifyType.UI_FORM, "Không thể đổi khu vực trong map này");
                return;
            }
        }

        getConnection().sendPacket(new SmOpenUiZone());
    }

}
