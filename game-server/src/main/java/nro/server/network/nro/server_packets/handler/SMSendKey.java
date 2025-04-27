package nro.server.network.nro.server_packets.handler;

import nro.server.configs.network.NetworkConfig;
import nro.server.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroCrypt;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

@ServerPacketCommand(ConstsCmd.GET_SESSION_ID)
public class SMSendKey extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) {
        System.out.println("write key");
        final byte[] keys = NroCrypt.sessionKey;
        this.writeByte(keys.length);
        int index;
        for (index = 0; index < keys.length; index++) {
            writeByte(keys[index]);
        }
        this.writeUTF(NetworkConfig.HOST);
        this.writeInt(NetworkConfig.PORT);
        this.writeByte(0);
    }
}
