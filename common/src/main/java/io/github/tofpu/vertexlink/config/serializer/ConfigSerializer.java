package io.github.tofpu.vertexlink.config.serializer;

import com.typesafe.config.Config;

public interface ConfigSerializer {
    String serialize(Config config);
    Config deserialize(String serializedConfig);

    static ConfigSerializer serializer() {
        return ConfigSerializerImpl.INSTANCE;
    }
}
