package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.main.ConfigServer;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

@ServerPacketCommand(ConstsCmd.UPDATE_DATA)
public class SMUpdateData extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) throws RuntimeException {
        this.writeBytes(ConfigServer.VERSION_DATA);
    }

}
