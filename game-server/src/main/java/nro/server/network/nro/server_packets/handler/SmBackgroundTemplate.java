package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.BACKGROUND_TEMPLATE)
public class SmBackgroundTemplate extends NroServerPacket {

    private final short id;
    private final byte[] data;

    public SmBackgroundTemplate(short id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeShort(id);
        writeInt(data.length);
        writeBytes(data);
    }
}
