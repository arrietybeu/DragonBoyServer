package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.CHAT_MAP)
public class SmChatMap extends NroServerPacket {

    private final int playerID;
    private final String message;

    public SmChatMap(int playerID, String message) {
        this.playerID = playerID;
        this.message = message;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        this.writeInt(playerID);
        this.writeUTF(message);
    }
}
