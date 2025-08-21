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

    private final String message;

    public SmChatMap(String message) {
        this.message = message;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        var playerID = con.getPlayerID();
        this.writeInt(playerID);
        this.writeUTF(message);
    }
}
