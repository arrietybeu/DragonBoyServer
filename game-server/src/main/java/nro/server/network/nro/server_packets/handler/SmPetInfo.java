package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.PET_INFO)
public class SmPetInfo extends NroServerPacket {
    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeByte(0);
    }
}
