package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;

/**
 * @author Arriety
 */

@ServerPacketCommand(ConstsCmd.GET_EFFDATA)
public class SmSendEffect extends NroServerPacket {

    private final int idEffect;
    private final byte type;
    private final byte[] data;
    private final byte[] imageData;

    public SmSendEffect(int idEffect, int type, byte[] data, byte[] imageData) {
        this.idEffect = idEffect;
        this.type = (byte) type;
        this.data = data;
        this.imageData = imageData;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        writeShort(idEffect);

        if (type == 0) {
            writeInt(data.length);
            writeBytes(data);
        } else {
            // TODO write new Boss effect
            writeInt(0);
        }

        writeByte(type);
        writeInt(imageData.length);
        writeBytes(imageData);
    }
}
