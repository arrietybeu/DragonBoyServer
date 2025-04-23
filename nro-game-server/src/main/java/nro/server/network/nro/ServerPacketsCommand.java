package nro.server.network.nro;

import java.util.HashMap;
import java.util.Map;

public class ServerPacketsCommand {

    private static Map<Class<? extends NroServerPacket>, Integer> commands = new HashMap<>();

    static int getOpcode(Class<? extends NroServerPacket> packetClass) {
        Integer opcode = commands.get(packetClass);
        if (opcode == null)
            throw new IllegalArgumentException("There is no opcode for " + packetClass + " defined.");

        return opcode;
    }

    private static void addPacketOpcode(int opcode, Class<? extends NroServerPacket> packetClass) {
        if (opcode < 0)
            return;

        if (commands.values().contains(opcode))
            throw new IllegalArgumentException(String.format("There already exists another packet with id 0x%02X", opcode));

        commands.put(packetClass, opcode);
    }

}
