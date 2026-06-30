package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public enum ConfigLoaderImpl implements ConfigLoader {
    INSTANCE;

    @Override
    public Config loadConfig() {
        return ConfigFactory.load();
    }
}
