package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;

public interface ConfigurationListener {
    ConfigurationListener IDENTITY = newConfig -> {};

    void onConfigurationUpdate(Config newConfig);
}