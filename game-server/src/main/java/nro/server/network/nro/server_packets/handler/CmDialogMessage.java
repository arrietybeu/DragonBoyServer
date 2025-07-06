package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.DIALOG_MESSAGE)
public class CmDialogMessage extends NroServerPacket {

    private final String message;

    public CmDialogMessage(String message) {
        this.message = message;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        this.writeUTF(message);
    }
}
