package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.repo.MapData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.ITEM_BACKGROUND)
public class SmItemBackground extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeBytes(MapData.getInstance().getDataBackgroundMapTemplates());
    }

}
