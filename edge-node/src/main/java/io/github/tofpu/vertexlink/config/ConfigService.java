package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;

public class ConfigService {
    private Config config;
    private final ConfigurationListener configurationListener;

    public ConfigService(Config config, ConfigurationListener configurationListener) {
        this.config = config;
        this.configurationListener = configurationListener;
    }

    public void updateConfig(Config newConfig) {
        this.config = newConfig;
        this.configurationListener.onConfigurationUpdate(newConfig);
    }

    public Config config() {
        return config;
    }
}
