package nro.commons.configuration;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

    String DEFAULT_VALUE = "DO_NOT_OVERWRITE_INITIALIAZION_VALUE";
    /**
     * Property name in configuration
     *
     * @return name of the property that will be used
     */
    String key();

    String defaultValue() default DEFAULT_VALUE;

}
