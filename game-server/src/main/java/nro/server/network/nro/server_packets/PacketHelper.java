package nro.server.network.nro.server_packets;

import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;

/**
 * @author Arriety
 */
public final class PacketHelper {
    public static NroServerPacket empty(int opcode) {
        return new NroServerPacket(opcode) {
            @Override
            protected void writeImpl(NroConnection con) {
            }
        };
    }
}
