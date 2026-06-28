package io.github.tofpu.vertexlink.grpc;

import io.github.tofpu.vertexlink.protos.TelemetryPayloadData;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;

public interface GrpcDataAdapter<T extends TelemetryPayload> {
    T deserialize(TelemetryPayloadData data);
    T deserialize(byte[] data);
    byte[] serialize(T data);
}
