package nro.server.services;

import nro.commons.network.NioServer;
import nro.commons.utils.SystemInfo;
import nro.server.GameServer;
import nro.server.utils.LogServer;
import nro.server.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class CommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

    public static void ActiveCommandLine() {
        try {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String _line = sc.nextLine();
                switch (_line) {
                    case "thread" ->
                            ThreadPoolManager.getInstance().getStats().forEach(line -> LOGGER.info(LogServer.ANSI_GREEN + "{}" + LogServer.ANSI_RESET, line));
                    case "session" ->
                            LOGGER.info("session size {}", GameServer.getNioServer().listAllConnections().size());
                    case "system_info" -> SystemInfo.logAll();
                    case "gc" -> System.gc();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
