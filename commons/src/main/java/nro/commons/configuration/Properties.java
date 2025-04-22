package nro.commons.configuration;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Properties {

    String keyPattern() default ".+";
}
