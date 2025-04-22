package nro.commons.configs;

import nro.commons.configuration.Property;

public class CommonsConfig {

    @Property(key = "commons.runnablestats.enable", defaultValue = "false")
    public static boolean RUNNABLESTATS_ENABLE;

    @Property(key = "commons.script_compiler.caching.enable", defaultValue = "true")
    public static volatile boolean SCRIPT_COMPILER_CACHING;
}
