package nro.server.network.nro.server_packets.handler;

import nro.server.configs.network.NetworkConfig;
import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.server_packets.ServerPacketCommand;

@ServerPacketCommand(ConstsCmd.GET_SESSION_ID)
public class SMSendKey extends NroServerPacket {

    @Override
    protected void writeImpl(NroConnection con) {
        final byte[] rawKey = con.getCrypt().sessionKey;

        byte[] encodedKey = new byte[rawKey.length];
        System.arraycopy(rawKey, 0, encodedKey, 0, rawKey.length);

        for (int i = 0; i < encodedKey.length - 1; i++) {
            encodedKey[i + 1] ^= encodedKey[i];
        }

        this.writeBytes(encodedKey.length);
        for (byte b : encodedKey) {
            this.writeBytes(b);
        }

        this.writeUTF(NetworkConfig.HOST);
        this.writeInt(NetworkConfig.PORT);
        this.writeBytes(0);
    }

}
