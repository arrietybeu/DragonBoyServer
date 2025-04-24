package nro.server.network.nro.server_packets;

import nro.server.network.nro.NroServerPacket;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerPacketsCommand {

    private static final Map<Class<? extends NroServerPacket>, Integer> commands = new HashMap<>();

    public static void init(String basePackage) {
        try {
            Reflections reflections = new Reflections(basePackage);
            Set<Class<?>> classes = reflections.getTypesAnnotatedWith(ServerPacketCommand.class);

            for (Class<?> clazz : classes) {
                if (NroServerPacket.class.isAssignableFrom(clazz)) {
                    ServerPacketCommand annotation = clazz.getAnnotation(ServerPacketCommand.class);
                    int opcode = annotation.value();

                    if (commands.containsValue(opcode)) {
                        throw new IllegalStateException("Duplicate opcode: " + opcode + " for class: " + clazz.getSimpleName());
                    }

                    //noinspection unchecked
                    commands.put((Class<? extends NroServerPacket>) clazz, opcode);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize server packet opcodes", e);
        }
    }

    public static int getOpcode(Class<? extends NroServerPacket> packetClass) {
        Integer opcode = commands.get(packetClass);
        if (opcode == null) {
            throw new IllegalArgumentException("No command found for packet class: " + packetClass.getSimpleName());
        }
        return opcode;
    }

}
