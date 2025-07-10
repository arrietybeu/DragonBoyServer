package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.data_holders.data.ItemData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.CMD_EXTRA_BIG)
public class SmCmdExtraBig extends NroServerPacket {
    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeByte(0);
        writeByte(ConfigServer.VERSION_DATA_ITEM);
        writeBytes(ItemData.getInstance().getDataItemTemplate());
    }
}
