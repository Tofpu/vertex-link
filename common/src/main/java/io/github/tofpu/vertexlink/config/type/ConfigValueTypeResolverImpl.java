package io.github.tofpu.vertexlink.config.type;

import io.github.tofpu.vertexlink.protos.ValueType;

public enum ConfigValueTypeResolverImpl implements ConfigValueTypeResolver {
    INSTANCE;

    @Override
    public ValueType resolveFrom(Object object) {
        if (object instanceof Boolean) {
            return ValueType.BOOLEAN;
        }
        throw new IllegalStateException("Unsupported type: " + object.getClass());
    }
}
