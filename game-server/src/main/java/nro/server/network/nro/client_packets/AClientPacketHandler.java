package nro.server.network.nro.client_packets;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import nro.server.network.nro.NroConnection.State;

@Retention(RetentionPolicy.RUNTIME)
public @interface AClientPacketHandler {

    byte command();

    State[] validStates() default {State.CONNECTED};

}
