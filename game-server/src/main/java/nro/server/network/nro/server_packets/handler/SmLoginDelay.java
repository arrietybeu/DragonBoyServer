package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.LOGIN_DE)
public class SmLoginDelay extends NroServerPacket {

    private final short delay;

    public SmLoginDelay(int delay) {
        this.delay = (short) delay;
    }

    @Override
    protected void writeImpl(NroConnection con) {
        writeShort(delay);
    }
}
