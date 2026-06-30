package io.github.tofpu.vertexlink.config.value;

import io.github.tofpu.vertexlink.protos.ValueType;

public interface ConfigValueConverter {
    byte[] convertObjectToBytes(ValueType valueType, Object object);
    Object convertBytesToObject(ValueType valueType, byte[] bytes);

    static ConfigValueConverter converter() {
        return ConfigValueConverterImpl.INSTANCE;
    }
}
