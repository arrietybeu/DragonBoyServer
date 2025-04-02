package nro.server.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Arriety
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface APacketHandler {
    byte value();// status msg
}
