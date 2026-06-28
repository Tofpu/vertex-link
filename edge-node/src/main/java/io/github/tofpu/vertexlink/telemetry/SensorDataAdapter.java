package io.github.tofpu.vertexlink.telemetry;

import org.h2.mvstore.WriteBuffer;

import java.nio.ByteBuffer;

public interface SensorDataAdapter<T> {
    int calculateMemory(T obj);

    default boolean isMemoryEstimationAllowed() {
        return true;
    }

    void write(WriteBuffer buff, T payload);

    T read(ByteBuffer buff);

    T[] createStorage(int size);
}
