package nro.server.configs;


import ch.qos.logback.classic.ClassicConstants;
import nro.commons.configuration.ConfigurableProcessor;
import nro.commons.utils.NetworkUtils;
import nro.commons.utils.PropertiesUtils;
import nro.server.GameServerError;
import nro.server.configs.main.ConfigServer;
import nro.server.configs.main.PacketConfig;
import nro.server.configs.main.ThreadConfig;
import nro.server.configs.network.NetworkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class Config {

    private static final List<Class<?>> CONFIGS = Arrays.asList(ConfigServer.class, ThreadConfig.class, NetworkConfig.class, PacketConfig.class);

    public static void load(Class<?>... allowedConfigs) {
        Properties properties = loadProperties();

        for (Class<?> config : allowedConfigs) {
            if (!CONFIGS.contains(config))
                throw new IllegalArgumentException(config + " is not an allowed config");
        }

        boolean processAllConfigs = allowedConfigs.length == 0;

        Set<String> unusedProperties = ConfigurableProcessor.process(properties, processAllConfigs ? CONFIGS.toArray() : allowedConfigs);
        if (processAllConfigs && !unusedProperties.isEmpty()) {
            removePropertiesUsedInLogbackXml(unusedProperties);
            unusedProperties.forEach(p -> LoggerFactory.getLogger(Config.class).warn("Config property {} is unknown and therefore ignored.", p));
        }

        if (NetworkConfig.CLIENT_CONNECT_ADDRESS.getAddress().isAnyLocalAddress()) {
            InetAddress localIPv4 = NetworkUtils.findLocalIPv4();
            if (localIPv4 == null)
                throw new GameServerError("No IP for Nro client advertisement configured and local IP discovery failed. Please configure gameserver.network.client.connect_address");
            NetworkConfig.CLIENT_CONNECT_ADDRESS = new InetSocketAddress(localIPv4, NetworkConfig.CLIENT_CONNECT_ADDRESS.getPort());
            LoggerFactory.getLogger(Config.class).info("No IP for Aion client advertisement configured, using {}", localIPv4.getHostAddress());
        }
    }

    private static void removePropertiesUsedInLogbackXml(Set<String> properties) {
        String logbackXml = System.getProperty(ClassicConstants.CONFIG_FILE_PROPERTY);
        if (logbackXml != null) {
            try {
                String logbackXmlContent = Files.readString(Path.of(logbackXml));
                properties.removeIf(property -> logbackXmlContent.contains("${" + property + '}'));
            } catch (IOException e) {
                LoggerFactory.getLogger(Config.class).error("", e);
            }
        }
    }

    private static Properties loadProperties() {
        Logger log = LoggerFactory.getLogger(Config.class);
        List<String> defaultsFolders = Arrays.asList(/*"./config/administration", */"resources/config/main", "resources/config/network");
        Properties defaults = new Properties();
        try {
            for (String configDir : defaultsFolders) {
                log.info("Loading default configuration values from: {}/*", configDir);
                PropertiesUtils.loadFromDirectory(defaults, configDir, false);
            }
            log.info("Loading: ./config/mygs.properties");
            Properties properties = PropertiesUtils.load("./config/mygs.properties", defaults);
            if (properties.isEmpty())
                log.info("No override properties found");
            return properties;
        } catch (Exception e) {
            throw new GameServerError("Can't load game-server configuration:", e);
        }
    }

}
