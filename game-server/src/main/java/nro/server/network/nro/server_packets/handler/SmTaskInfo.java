package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.TASK_GET)
public class SmTaskInfo extends NroServerPacket {
    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeShort(30); // Task ID
        writeByte(0); // Task index
        writeUTF("Arriety");
        writeUTF("Task");
        writeByte(0);
        writeShort(0);
    }
}
