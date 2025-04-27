package nro.server.configs.network;

import nro.commons.configuration.Property;

import java.net.InetSocketAddress;

public class NetworkConfig {

    @Property(key = "game-server.network.nio.threads", defaultValue = "1")
    public static int NIO_READ_WRITE_THREADS;

    @Property(key = "game-server.network.client.connect_address", defaultValue = "127.0.0.1:14445")
    public static InetSocketAddress CLIENT_CONNECT_ADDRESS;

    @Property(key = "game-server.network.client.socket_address", defaultValue = "127.0.0.1:14445")
    public static InetSocketAddress CLIENT_SOCKET_ADDRESS;

    @Property(key = "game-server.network.host", defaultValue = "127.0.0.1")
    public static String HOST;

    @Property(key = "game-server.network.port", defaultValue = "14445")
    public static int PORT;

    @Property(key = "game-server.network.packet.processor.threads.min", defaultValue = "4")
    public static int PACKET_PROCESSOR_MIN_THREADS;

    @Property(key = "game-server.network.packet.processor.threads.max", defaultValue = "4")
    public static int PACKET_PROCESSOR_MAX_THREADS;

    /**
     * Threshold that will be used to decide when extra threads are not needed. (it doesn't have any effect if min threads == max threads)
     */
    @Property(key = "game-server.network.packet.processor.threshold.kill", defaultValue = "3")
    public static int PACKET_PROCESSOR_THREAD_KILL_THRESHOLD;

    /**
     * Threshold that will be used to decide when extra threads should be spawned. (it doesn't have any effect if min threads == max threads)
     */
    @Property(key = "game-server.network.packet.processor.threshold.spawn", defaultValue = "50")
    public static int PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD;

    @Property(key = "game-server.network.server.read.buffer.size", defaultValue = "2048")
    public static int READ_BUFFER_SIZE;

    @Property(key = "game-server.network.server.write.buffer.size", defaultValue = "2048")
    public static int WRITE_BUFFER_SIZE;

    @Property(key = "game-server.network.flood.connections", defaultValue = "false")
    public static boolean ENABLE_FLOOD_CONNECTIONS;

    @Property(key = "game-server.network.flood.tick", defaultValue = "1000")
    public static int FLOOD_TICK;

    @Property(key = "game-server.network.flood.short.tick", defaultValue = "10")
    public static int FLOOD_STICK;

    @Property(key = "game-server.network.flood.short.warn", defaultValue = "10")
    public static int FLOOD_SWARN;

    @Property(key = "game-server.network.flood.short.reject", defaultValue = "20")
    public static int FLOOD_SREJECT;

    @Property(key = "game-server.network.flood.long.warn", defaultValue = "30")
    public static int FLOOD_LWARN;

    @Property(key = "game-server.network.flood.long.tick", defaultValue = "60")
    public static int FLOOD_LTICK;

    @Property(key = "game-server.network.flood.long.reject", defaultValue = "60")
    public static int FLOOD_LREJECT;

}
