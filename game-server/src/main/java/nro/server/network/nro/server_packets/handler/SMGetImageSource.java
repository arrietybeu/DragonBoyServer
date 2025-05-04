package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.VersionImageData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

@ServerPacketCommand(ConstsCmd.GET_IMAGE_SOURCE)
public class SMGetImageSource extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) {
        int zoomLevel = con.getSessionInfo().getClientDeviceInfo().getZoomLevel();
        int size = VersionImageData.getInstance().getVersionImage(zoomLevel);
        this.writeByte(0);
        this.writeInt(size);
        System.out.println("size: " + size);
    }

}
