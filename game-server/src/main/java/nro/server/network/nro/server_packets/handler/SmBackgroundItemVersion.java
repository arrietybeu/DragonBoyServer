package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.repo.ResourcesData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.BGITEM_VERSION)
public class SmBackgroundItemVersion extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        var res = ResourcesData.getInstance();
        var zoomLevel = con.getSessionInfo().getClientDeviceInfo().getZoomLevel();

        byte[][] backgroundVersion = res.getBackgroundVersion();
        byte[] data = backgroundVersion[zoomLevel - 1];

        this.writeShort(data.length);
        this.writeBytes(data);
    }
}
