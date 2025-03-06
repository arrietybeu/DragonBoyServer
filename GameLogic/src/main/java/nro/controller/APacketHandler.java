package nro.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Arriety
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface APacketHandler {

    byte value();// type msg

}
