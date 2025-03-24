package nro;

import nro.server.ServerManager;
import nro.server.system.LogServer;

public class DragonBall {

    public static void main(String[] args) {
        ServerManager.launch();
        LogServer.DebugLogic("Launching Dragon Ball...");
    }
}