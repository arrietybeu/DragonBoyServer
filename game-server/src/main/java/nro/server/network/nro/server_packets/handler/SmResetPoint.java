package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.RESET_POINT)
public class SmResetPoint extends NroServerPacket {

    private final short x;
    private final short y;

    public SmResetPoint(final int x, final int y) {
        this.x = (short) x;
        this.y = (short) y;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeShort(x);
        writeShort(y);
    }
}
