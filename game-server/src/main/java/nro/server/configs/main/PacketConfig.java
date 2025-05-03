package nro.server.configs.main;

import nro.commons.configuration.Property;

public class PacketConfig {

    @Property(key = "server.packet.command", defaultValue = "nro.server.network.nro.server_packets.handler")
    public static String SERVER_PACKET_COMMAND;

    @Property(key = "client.packet.command", defaultValue = "nro.server.network.nro.client_packets.handler")
    public static String CLIENT_PACKET_COMMAND;

}
