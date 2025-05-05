package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.configs.network.NetworkConfig;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

@ServerPacketCommand(ConstsCmd.NOT_LOGIN)
public class SMNotLogin extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) {
        this.writeBytes(2);
        this.writeUTF(NetworkConfig.HOST_PORT + ":0,0,0");
        this.writeBytes(1);
    }

}
