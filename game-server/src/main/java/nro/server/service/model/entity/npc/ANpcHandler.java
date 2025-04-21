package nro.server.service.model.entity.npc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ANpcHandler {
    int[] value();
}