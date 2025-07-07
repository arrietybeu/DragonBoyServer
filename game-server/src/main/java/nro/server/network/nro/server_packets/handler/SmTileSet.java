package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.data_holders.data.MapData;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

/**
 * @author Arriety
 */
@ServerPacketCommand(ConstsCmd.TILE_SET)
public class SmTileSet extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        writeBytes(MapData.getInstance().tileSetInfoData);
    }
}
