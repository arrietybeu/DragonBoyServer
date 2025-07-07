package nro.server.network.nro.client_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroClientPacket;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.client_packets.AClientPacketHandler;
import nro.server.network.nro.server_packets.handler.SmBackgroundTemplate;
import nro.server.utils.FileNio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author Arriety
 */
@AClientPacketHandler(command = ConstsCmd.BACKGROUND_TEMPLATE, validStates = {NroConnection.State.AUTHED})
public class CmBackgroundTemplate extends NroClientPacket {

    protected static final Logger log = LoggerFactory.getLogger(CmBackgroundTemplate.class);

    private short id;

    public CmBackgroundTemplate(int command, Set<NroConnection.State> validStates) {
        super(command, validStates);
    }

    @Override
    protected void readImpl() {
        id = this.readShort();
    }

    @Override
    protected void runImpl() {
        var path = "resources/x" + getConnection().getSessionInfo().getClientDeviceInfo().getZoomLevel() + "/image_background/" + id + ".png";
        byte[] data = FileNio.loadDataFile(path);
        if (data == null) {
            log.error("Failed to load image background image for id: {}", id);
            return;
        }
        sendPacket(new SmBackgroundTemplate(id, data));
    }
}
