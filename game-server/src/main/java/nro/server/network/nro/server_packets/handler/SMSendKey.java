package nro.server.network.nro.server_packets.handler;

import nro.commons.network.Crypt;
import nro.server.configs.network.NetworkConfig;
import nro.server.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

@ServerPacketCommand(ConstsCmd.GET_SESSION_ID)
public class SMSendKey extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) {
        final byte[] keys = Crypt.sessionKey;
        this.writeByte(keys.length);

        for (byte b : keys) {
            writeByte(b);
        }

        this.writeUTF(NetworkConfig.HOST);
        this.writeInt(NetworkConfig.PORT);
        this.writeByte(0);
    }
}
