package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.model.template.session.ClientDeviceInfo;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.utils.FileNio;

import java.util.Objects;

@ServerPacketCommand(ConstsCmd.GET_IMAGE_SOURCE2)
public class SMGetImageSources2 extends NroServerPacket {

    @Override
    protected void writeImpl(final NroConnection con) {
        final ClientDeviceInfo session = con.getSessionInfo().getClientDeviceInfo();

        final var zoomLevel = session.getZoomLevel();

        String path = "resources/x" + zoomLevel + "/image_source/image_source";

        this.writeBytes(Objects.requireNonNull(FileNio.loadDataFile(path)));
    }

}
