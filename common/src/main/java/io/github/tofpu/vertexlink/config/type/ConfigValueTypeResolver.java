package io.github.tofpu.vertexlink.config.type;

import io.github.tofpu.vertexlink.protos.ValueType;

public interface ConfigValueTypeResolver {

    static ConfigValueTypeResolver resolver() {
        return ConfigValueTypeResolverImpl.INSTANCE;
    }

    ValueType resolveFrom(Object object);
}
