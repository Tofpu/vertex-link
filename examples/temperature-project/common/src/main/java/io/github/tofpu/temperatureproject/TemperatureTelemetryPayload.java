package io.github.tofpu.temperatureproject;

import io.github.tofpu.vertexlink.telemetry.NodeId;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;

public record TemperatureTelemetryPayload(
        NodeId nodeId,
        long id,
        int value
) implements TelemetryPayload {
    @Override
    public String type() {
        return "temperature";
    }
}
