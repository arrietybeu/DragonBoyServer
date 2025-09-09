package nro.server.services;

import lombok.NoArgsConstructor;
import nro.commons.database.DatabaseFactory;
import nro.commons.utils.ExitCode;
import nro.commons.utils.SystemInfo;
import nro.commons.utils.concurrent.RunnableStatsManager;
import nro.server.GameServer;
import nro.server.controllers.BannedIpController;
import nro.server.data_holders.data.DartData;
import nro.server.data_holders.data.PartData;
import nro.server.engine.GameWorld;
import nro.server.engine.quest.QuestEngine;
import nro.server.model.templates.data.PartTemplate;
import nro.server.utils.ThreadPoolManager;
import nro.server.utils.factory.IDFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Scanner;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CommandService {

    private static volatile boolean isActive = false;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandService.class);

    public static void initCommandLine() {
        isActive = true;
        Thread.startVirtualThread(CommandService::ActiveCommandLine);
    }

    public static void ActiveCommandLine() {
        isActive = true;
        try (Scanner sc = new Scanner(System.in)) {
            while (isActive) {
                try {
                    if (!sc.hasNextLine()) {
                        LOGGER.warn("Command line input closed. Stopping CommandService.");
                        isActive = false;
                        break;
                    }
                    String _line = sc.nextLine().trim();
                    switch (_line) {
                        // case "map_info" -> LOGGER.info(World.getInstance().logInfo());
                        case "ecs_info" -> GameWorld.getInstance().logWorldSummary();
                        case "entity_info" -> GameWorld.getInstance().logEntitiesWithComponentsJson(100);
                        case "id" -> LOGGER.info(IDFactory.getInstance().getDebugInfo(100));
                        case "reload_ban_ip" -> BannedIpController.reload();
                        case "thread" -> LOGGER.info(ThreadPoolManager.getInstance().getStats());
                        case "database_pool" -> LOGGER.info(DatabaseFactory.getStatsPool());
                        case "session" ->
                            LOGGER.info("session size {}", GameServer.getNioServer().listAllConnections().size());
                        case "system_info" -> SystemInfo.logAll();
                        case "gc" -> System.gc();
                        case "dump_packet" -> RunnableStatsManager.dumpClassStats();
                        case "exit" -> GameServer.initShutdown(ExitCode.NORMAL, 5);
                        case "dart" ->
                            DartData.getInstance().darts.forEach(dartTemplate -> LOGGER.info(dartTemplate.toString()));
                        case "part" -> System.out.println("PartData size: " + buildNrPartData().length);
                        case "task" -> QuestEngine.getInstance().logTask0Info();
                    }
                } catch (Exception exception) {
                    LOGGER.error("", exception);
                    break;
                }
            }
        }
    }

    public static void closeCommandLine() {
        isActive = false;
        LOGGER.info("CommandLine closed");
    }

    private static byte[] buildNrPartData() {
        ByteBuffer buf = ByteBuffer.allocate(100_000);
        List<PartTemplate> parts = PartData.getInstance().templates;

        buf.putShort((short) parts.size());

        for (PartTemplate part : parts) {
            System.out.println("Part: id: " + part.id() + ", type: " + part.type());
            buf.put((byte) part.type());
            PartTemplate.PartImage[] pi = part.data();
            for (PartTemplate.PartImage img : pi) {
                System.out.println("PartImage: id: " + part.id() + " icon: " + img.icon() + ", dx: " + img.dx()
                        + ", dy: " + img.dy());
                buf.putShort(img.icon());
                buf.put(img.dx());
                buf.put(img.dy());
            }
        }

        byte[] out = new byte[buf.position()];
        buf.flip();
        buf.get(out);
        return out;
    }
}
