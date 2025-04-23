package nro.server.config.network;

import nro.commons.configuration.Properties;
import nro.commons.configuration.Property;
import nro.server.network.nro.NroClientPacket;

import java.util.Map;

public class PacketFloodFilterConfig {


    @Property(key = "gameserver.network.pff.mode", defaultValue = "1")
    public static int PFF_MODE;

    @Properties(keyPattern = "^gameserver\\.network\\.pff\\.packet\\.(0[xX][0-9a-fA-F]+)$")
    public static Map<Integer, Integer> THRESHOLD_MILLIS_BY_PACKET_OPCODE;

    /**
     * @return The allowed delay in milliseconds in which two packets of the given type may be sent from one client.
     */
    public static int getAllowedMillisBetweenPackets(NroClientPacket packet) {
        return THRESHOLD_MILLIS_BY_PACKET_OPCODE.getOrDefault(packet.getCommand(), 0);
    }
}
