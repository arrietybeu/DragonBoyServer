package nro.server.configs.network;

import nro.commons.configuration.Property;

import java.net.InetSocketAddress;

public class NetworkConfig {

    @Property(key = "game-server.network.client.connect_address", defaultValue = "127.0.0.1:14445")
    public static InetSocketAddress CLIENT_CONNECT_ADDRESS;

    @Property(key = "game-server.network.client.socket_address", defaultValue = "0.0.0.0:7777")
    public static InetSocketAddress CLIENT_SOCKET_ADDRESS;

}
