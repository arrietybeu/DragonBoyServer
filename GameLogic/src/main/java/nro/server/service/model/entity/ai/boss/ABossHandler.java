package nro.server.service.model.entity.ai.boss;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ABossHandler {
    int value();
}
