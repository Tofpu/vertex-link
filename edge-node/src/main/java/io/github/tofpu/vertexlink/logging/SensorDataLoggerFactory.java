package io.github.tofpu.vertexlink.logging;

import io.github.tofpu.vertexlink.logging.impl.MVMapSensorDataLogger;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;

public interface SensorDataLoggerFactory<T extends TelemetryPayload> {
    MVMapSensorDataLogger<T> createSensorDataLogger();
}
