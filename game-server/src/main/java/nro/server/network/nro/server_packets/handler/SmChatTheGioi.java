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

    private final String text;
    private String namePlayer;

    public SmChatTheGioi(String text) {
        this.text = text;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        String name = (namePlayer != null) ? namePlayer : "";
        writeUTF(name);
        writeUTF(text);
    }
}
