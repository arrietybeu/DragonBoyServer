package nro.login;

import lombok.Getter;
import lombok.Setter;
import nro.login.model.Account;
import nro.login.network.game_server.GameServerConnection;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class GameServerInfo {

    private final byte id;

    private final String ipMask;

    private final String password;

    private byte[] ip = {0, 0, 0, 0};

    private int port;

    private GameServerConnection connection;

    private byte minAccessLevel;

    private int maxPlayers;

    private final Map<Integer, Account> accountsOnGameServer = new HashMap<>();

    public GameServerInfo(byte id, String ipMask, String password) {
        this.id = id;
        this.ipMask = ipMask;
        this.password = password;
    }
}
