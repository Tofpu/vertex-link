package io.github.tofpu.vertexlink.telemetry;

public interface SensorDataAdapterOld<S, D> {
    S serialize(D deserializedData);

    D deserialize(S serializedData);
}
