package io.github.tofpu.vertexlink.grpc;

import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;

public interface GrpcDataAdapter<T extends TelemetryPayload> {
    byte[] serialize(T payload);
}
