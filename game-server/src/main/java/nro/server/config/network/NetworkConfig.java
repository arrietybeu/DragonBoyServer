package nro.server.config.network;

import nro.commons.configuration.Property;

import java.net.InetSocketAddress;

public class NetworkConfig {

    @Property(key = "game-server.network.client.connect_address", defaultValue = "0.0.0.0:7777")
    public static InetSocketAddress CLIENT_CONNECT_ADDRESS;

    @Property(key = "game-server.network.nio.threads", defaultValue = "1")
    public static int NIO_READ_WRITE_THREADS;

    public static InetSocketAddress CLIENT_SOCKET_ADDRESS;
}
