package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.UPDATE_ACTIVEPOINT)
public class SmActivePoint extends NroServerPacket {

    private final int activePoint;

    public SmActivePoint(int activePoint) {
        this.activePoint = activePoint;
        if (activePoint < 0) {
            throw new IllegalArgumentException("Active point cannot be negative: " + activePoint);
        }
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeInt(activePoint);
    }
}
