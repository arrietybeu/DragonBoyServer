package nro.server;

import nro.commons.utils.SystemInfo;
import nro.server.manager.ServerManager;
import nro.server.system.LogServer;

public class DragonBall {

    public static void main(String[] args) {
        ServerManager.launch();
        SystemInfo.logAll();
        LogServer.DebugLogic("Launching Dragon Ball...");
    }

}