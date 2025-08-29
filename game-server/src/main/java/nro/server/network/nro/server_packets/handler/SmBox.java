package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.PacketHelper;
import nro.server.network.nro.server_packets.ServerPacketCommand;

import java.io.IOException;
import java.util.List;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.BOX)
public class SmBox extends NroServerPacket {

    private final List<Integer> items;
    private final byte type;

    public SmBox(List<Integer> items, int type) {
        if (items == null) throw new IllegalArgumentException("items is null");
        if (items.isEmpty()) throw new IllegalArgumentException("items is empty");
        this.items = items;
        this.type = (byte) type;
    }

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException, IOException {
        writeByte(type);
        switch (type) {
            case 0 -> PacketHelper.sendInventoryForPlayer(this, items);
            case 1 -> {
                // ccj do
            }
        }
    }
}
