package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.PLAYER_REMOVE)
public class SmPlayerRemove extends NroServerPacket {

    private final int playerID;

    public SmPlayerRemove(int playerID) {
        this.playerID = playerID;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        writeInt(playerID);
    }
}
