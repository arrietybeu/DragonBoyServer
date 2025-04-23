package nro.server.config.network;

import nro.commons.configuration.Property;

import java.net.InetSocketAddress;

public class NetworkConfig {

    @Property(key = "game-server.network.client.connect_address", defaultValue = "0.0.0.0:7777")
    public static InetSocketAddress CLIENT_CONNECT_ADDRESS;

    /**
     * Số lượng luồng (threads) dành riêng cho hệ thống NIO (Selector) xử lý I/O (read/write)
     */
    @Property(key = "game-server.network.nio.threads", defaultValue = "1")
    public static int NIO_READ_WRITE_THREADS;

    /**
     * Số lượng **tối thiểu** của luồng xử lý gói tin (packets) từ client game
     */
    @Property(key = "gameserver.nro.utils.test.network.packet.processor.threads.min", defaultValue = "4")
    public static int PACKET_PROCESSOR_MIN_THREADS;

    /**
     * Số lượng tối đa
     */
    @Property(key = "gameserver.nro.utils.test.network.packet.processor.threads.max", defaultValue = "4")
    public static int PACKET_PROCESSOR_MAX_THREADS;

    @Property(key = "gameserver.nro.utils.test.network.packet.processor.threshold.spawn", defaultValue = "50")
    public static int PACKET_PROCESSOR_THREAD_SPAWN_THRESHOLD;

    @Property(key = "gameserver.nro.utils.test.network.packet.processor.threshold.kill", defaultValue = "3")
    public static int PACKET_PROCESSOR_THREAD_KILL_THRESHOLD;

    @Property(key = "gameserver.network.client.socket_address", defaultValue = "0.0.0.0:14445")
    public static InetSocketAddress CLIENT_SOCKET_ADDRESS;

}
