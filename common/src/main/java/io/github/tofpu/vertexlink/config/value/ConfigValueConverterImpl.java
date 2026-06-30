package io.github.tofpu.vertexlink.config.value;

import io.github.tofpu.vertexlink.protos.ValueType;

public enum ConfigValueConverterImpl implements ConfigValueConverter {
    INSTANCE;

    @Override
    public byte[] convertObjectToBytes(ValueType valueType, Object object) {
        if (valueType == ValueType.BOOLEAN) {
            int booleanToIntValue = (boolean) object ? 1 : 0;
            return new byte[]{(byte) booleanToIntValue};
        }
        throw new IllegalStateException("Unsupported type: " + valueType);
    }

    @Override
    public Object convertBytesToObject(ValueType valueType, byte[] bytes) {
        if (valueType == ValueType.BOOLEAN) {
            return bytes[0] == 1;
        }
        throw new IllegalStateException("Unsupported type: " + valueType);
    }
}
