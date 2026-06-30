package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;

public interface ConfigLoader {
    static ConfigLoader loader() {
        return ConfigLoaderImpl.INSTANCE;
    }

    Config loadConfig();
}
