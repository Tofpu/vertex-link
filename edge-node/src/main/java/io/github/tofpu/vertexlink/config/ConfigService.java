package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ConfigService {
    private static final String ROOT_PATH_NAME = "edge-node";

    private Config config;
    private final ConfigurationListener configurationListener;

    public ConfigService(Config config, ConfigurationListener configurationListener) {
        this.config = verifyValidity(config);
        this.configurationListener = configurationListener;
    }

    public Config verifyValidity(Config config) {
        config.checkValid(ConfigFactory.defaultReference(), ROOT_PATH_NAME);
        return config;
    }

    public void updateConfig(Config newConfig) {
        this.config = verifyValidity(newConfig);
        this.configurationListener.onConfigurationUpdate(newConfig);
    }

    public Config config() {
        return config;
    }
}
