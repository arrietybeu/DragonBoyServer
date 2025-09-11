package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.CHAT_THEGIOI_SERVER)
public class SmChatTheGioi extends NroServerPacket {

    private final byte type;
    private final String text;

    public SmChatTheGioi(byte type, String text) {
        this.type = type;
        this.text = text;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        switch (type) {
            case 0 -> {
                writeUTF("");
                writeUTF(text);
            }
            case 1 -> {

            }
        }
    }
}
