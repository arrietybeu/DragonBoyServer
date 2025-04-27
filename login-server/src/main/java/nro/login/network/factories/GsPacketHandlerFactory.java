package nro.login.network.factories;

import nro.login.network.game_server.GameServerClientPacket;
import nro.login.network.game_server.GameServerConnection;
import nro.login.network.game_server.client_packets.CM_GS_AUTH;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nro.login.network.game_server.GameServerConnection.State;

import java.nio.ByteBuffer;

public class GsPacketHandlerFactory {

    private static final Logger log = LoggerFactory.getLogger(GsPacketHandlerFactory.class);

    private static void unknownPacket(State state, int id) {
        log.warn("Unknown packet received from Game Server: 0x{} state={}", id, state.toString());
    }

    public static GameServerClientPacket handle(ByteBuffer data, GameServerConnection client) {
        GameServerClientPacket msg = null;

        State state = client.getState();

        int id = data.get() & 0xff;
        switch (state) {
            case CONNECTED -> {
                switch (id) {
                    case 0:
                        msg = new CM_GS_AUTH();
                        break;
                    default:
                        unknownPacket(state, id);
                }
            }
        }

        if (msg != null) {
            msg.setConnection(client);
            msg.setBuffer(data);
        }
        return msg;
    }
}

