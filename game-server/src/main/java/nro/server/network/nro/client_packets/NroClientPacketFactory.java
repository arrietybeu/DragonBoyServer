package nro.server.network.nro.client_packets;

import nro.server.network.nro.NroConnection;
import nro.server.network.nro.NroClientPacket;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NroClientPacketFactory {

    private static final Logger log = LoggerFactory.getLogger(NroClientPacketFactory.class);

    private static final Map<Integer, PacketInfo<? extends NroClientPacket>> packetMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static void init(String path) {
        Reflections reflections = new Reflections(path);
        for (Class<?> cls : reflections.getTypesAnnotatedWith(AClientPacketHandler.class)) {
            if (NroClientPacket.class.isAssignableFrom(cls)) {
                AClientPacketHandler handler = cls.getAnnotation(AClientPacketHandler.class);
                if (handler == null) {
                    throw new IllegalArgumentException("Handler annotation is missing on class: " + cls.getName());
                }
                NroClientPacketFactory.register((Class<? extends NroClientPacket>) cls, handler.command(), handler.validStates());
            }
        }
    }

    private static void register(Class<? extends NroClientPacket> packetClass, int command, NroConnection.State... validStates) {
        packetMap.put(command, new PacketInfo<>(packetClass, command, validStates));
    }

    public static NroClientPacket createPacket(ByteBuffer data, NroConnection client) {
//        int command = data.get() & 0xFF;
        int command = data.get();

        System.out.println("create packet for command: " + command);

        PacketInfo<? extends NroClientPacket> info = packetMap.get(command);
        if (info == null || !info.isValid(client.getState())) {
            log.warn("Unknown or invalid packet command: {} state: {}", command, client.getState());
            return null;
        }
        try {
            NroClientPacket packet = info.constructor.newInstance(command, info.validStates);
            packet.setConnection(client);
            packet.setBuffer(data.slice());
            return packet;
        } catch (Exception e) {
            log.error("Failed to instantiate packet: {}", command, e);
            return null;
        }
    }

    private static class PacketInfo<T extends NroClientPacket> {
        private final Constructor<T> constructor;
        private final Set<NroConnection.State> validStates;

        public PacketInfo(Class<T> clazz, int command, NroConnection.State... states) {
            try {
                this.constructor = clazz.getConstructor(int.class, Set.class);
                this.validStates = EnumSet.of(states[0], states);
            } catch (Exception e) {
                throw new RuntimeException("Invalid packet constructor", e);
            }
        }

        boolean isValid(NroConnection.State state) {
            return validStates.contains(state);
        }

    }

}
