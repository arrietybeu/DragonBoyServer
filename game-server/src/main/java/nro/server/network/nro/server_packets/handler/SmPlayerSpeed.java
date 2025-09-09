package nro.server.network.nro.server_packets.handler;

import nro.commons.consts.ConstsCmd;
import nro.server.network.nro.server_packets.ServerPacketCommand;
import nro.server.network.nro.NroServerPacket;
import nro.server.network.nro.NroConnection;

@ServerPacketCommand(ConstsCmd.PLAYER_SPEED)
public class SmPlayerSpeed extends NroServerPacket {

    private final int playerId;
    private final byte speed;

    public SmPlayerSpeed(int playerId, byte speed) {
        this.playerId = playerId;
        this.speed = speed;
    }

    @Override
    protected void writeImpl(NroConnection con) {
        writeInt(playerId);
        writeByte(speed);
    }

}
