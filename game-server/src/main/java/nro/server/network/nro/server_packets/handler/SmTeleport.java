package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.TELEPORT)
public class SmTeleport extends NroServerPacket {

    private final int playerId;
    private final byte teleport;

    public SmTeleport(int playerId, byte teleport) {
        this.playerId = playerId;
        this.teleport = teleport;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeInt(playerId);
        writeByte(teleport);
    }
}
