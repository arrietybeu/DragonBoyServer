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
        con.getCrypt().init();
        final byte[] keys = NroCrypt.sessionKey;
        System.out.println("size cua key: " + keys.length);
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
