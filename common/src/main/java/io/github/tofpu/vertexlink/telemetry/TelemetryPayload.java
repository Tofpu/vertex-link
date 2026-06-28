package io.github.tofpu.vertexlink.telemetry;

public interface TelemetryPayload {
    NodeId nodeId();
    long id();
    String type();
}
