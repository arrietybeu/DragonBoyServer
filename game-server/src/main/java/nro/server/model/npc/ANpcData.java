package nro.server.model.npc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Arriety
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ANpcData {
    int[] value();
}