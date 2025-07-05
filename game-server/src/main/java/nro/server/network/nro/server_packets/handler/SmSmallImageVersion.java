package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.ResourcesData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.SMALLIMAGE_VERSION)
public class SmSmallImageVersion extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {

        var res = ResourcesData.getInstance();
        var zoomLevel = con.getSessionInfo().getClientDeviceInfo().getZoomLevel();
        byte[] smallVersion = res.getDataSmallVersion().get(zoomLevel);

        this.writeShort(smallVersion.length);
        this.writeBytes(smallVersion);
    }

}
