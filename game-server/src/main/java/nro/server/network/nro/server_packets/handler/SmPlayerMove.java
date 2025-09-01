package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.PLAYER_MOVE)
public class SmPlayerMove extends NroServerPacket {

    private final int playerId;
    private final short newX;
    private final short newY;

    public SmPlayerMove(int playerId, short newX, short newY) {
        this.playerId = playerId;
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        System.out.println("write player di chuyen for id : " + playerId + " newX: " + newX + " newY: " + newY);
        writeInt(playerId);
        writeShort(newX);
        writeShort(newY);
    }
}
