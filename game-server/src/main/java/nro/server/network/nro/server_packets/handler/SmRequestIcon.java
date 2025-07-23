package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.utils.FileNio;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.REQUEST_ICON)
public class SmRequestIcon extends NroServerPacket {

    private final int iconId;

    public SmRequestIcon(int iconId) {
        this.iconId = iconId;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        String path = "resources/x" + con.getSessionInfo().getClientDeviceInfo().getZoomLevel() + "/icon/" + iconId + ".png";
        byte[] data = FileNio.loadDataFile(path);
        writeInt(iconId);
        if (data != null) {
            writeInt(data.length);
            writeBytes(data);
        } else {
            writeInt(0);
        }
    }
}
