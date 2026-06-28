package io.github.tofpu.temperatureproject;

import io.github.tofpu.vertexlink.snowflakeidgenerator.SnowflakeGenerator;
import io.github.tofpu.vertexlink.telemetry.NodeId;
import io.github.tofpu.vertexlink.telemetry.SensorDataIngestor;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

class TemperatureSensorDataIngestor implements SensorDataIngestor<TemperatureTelemetryPayload> {
    private static final ThreadLocalRandom THREAD_LOCAL_RANDOM = ThreadLocalRandom.current();
    private final UUID nodeId;

    public TemperatureSensorDataIngestor(UUID nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public TemperatureTelemetryPayload readCurrentData() {
        return new TemperatureTelemetryPayload(
                NodeId.wrap(nodeId),
                SnowflakeGenerator.create(1).nextId(),
                THREAD_LOCAL_RANDOM.nextInt(100)
        );
    }
}
