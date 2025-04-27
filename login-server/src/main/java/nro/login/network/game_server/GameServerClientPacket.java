package nro.login.network.game_server;

import nro.commons.network.packet.BaseClientPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GameServerClientPacket extends BaseClientPacket<GameServerConnection> {

    private static final Logger log = LoggerFactory.getLogger(GameServerClientPacket.class);

    public GameServerClientPacket() {
        super(0);
    }

    @Override
    public final void run() {
        try {
            runImpl();
        } catch (Throwable e) {
            log.warn("error handling gs ({}) message {}", getConnection().getIP(), this, e);
        }
    }

    protected void sendPacket(GameServerPacket msg) {
        getConnection().sendPacket(msg);
    }
}
