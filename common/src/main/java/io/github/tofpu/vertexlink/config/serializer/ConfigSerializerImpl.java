package io.github.tofpu.vertexlink.config.serializer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public enum ConfigSerializerImpl implements ConfigSerializer {
    INSTANCE;

    @Override
    public String serialize(Config config) {
        return config.root().render();
    }

    @Override
    public Config deserialize(String serializedConfig) {
        return ConfigFactory.parseString(serializedConfig);
    }
}
