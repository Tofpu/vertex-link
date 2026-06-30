package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;

public interface ConfigurationListener {
    void onConfigurationUpdate(Config newConfig);
}