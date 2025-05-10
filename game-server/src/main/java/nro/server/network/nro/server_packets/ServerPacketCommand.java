package nro.server.network.nro.server_packets;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author Arriety
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerPacketCommand {
    byte value();
}
