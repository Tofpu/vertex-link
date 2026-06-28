package io.github.tofpu.vertexlink.telemetry;

public interface SensorDataIngestor<T extends TelemetryPayload> {
    T readCurrentData();
}
