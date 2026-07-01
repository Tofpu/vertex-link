package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;

import java.util.function.Function;

public class ConfigService {
    public static final String ROOT_PATH_NAME = "edge-node";
    private static final String DELIMITER = ".";

    private Config config;
    private final ConfigurationListener configurationListener;

    public ConfigService(Config config, ConfigurationListener configurationListener) {
        this.config = verifyValidity(config);
        this.configurationListener = configurationListener;
    }

    Config verifyValidity(Config config) {
        config.checkValid(ConfigFactory.defaultReference(), ROOT_PATH_NAME);
        return config;
    }

    public void updateConfig(Config newConfig) {
        this.config = verifyValidity(newConfig);
        incrementConfigVersion();
        this.configurationListener.onConfigurationUpdate(newConfig);
    }

    public void updateConfig(Function<Config, Config> configConsumer) {
        Config newConfig = configConsumer.apply(config);
        updateConfig(newConfig);
    }

    private void incrementConfigVersion() {
        int version = configVersion();
        int updatedVersion = version + 1;
        this.config = config.withValue(withLibraryPath("version"), ConfigValueFactory.fromAnyRef(updatedVersion));
    }

    private String withLibraryPath(String path) {
        return ROOT_PATH_NAME + DELIMITER + path;
    }

    public int configVersion() {
        return this.config.getInt(withLibraryPath("version"));
    }

    public Config config() {
        return config;
    }
}
