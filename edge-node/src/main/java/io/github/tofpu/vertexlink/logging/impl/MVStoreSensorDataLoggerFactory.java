package io.github.tofpu.vertexlink.logging.impl;

import io.github.tofpu.vertexlink.logging.SensorDataLoggerFactory;
import io.github.tofpu.vertexlink.logging.TelemetryPayloadType;
import io.github.tofpu.vertexlink.telemetry.SensorDataAdapter;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

public class MVStoreSensorDataLoggerFactory<T extends TelemetryPayload> implements SensorDataLoggerFactory<T> {
    private final SensorDataAdapter<T> sensorDataAdapter;
    private final MVStore mvStore;

    public MVStoreSensorDataLoggerFactory(SensorDataAdapter<T> sensorDataAdapter, MVStore mvStore) {
        this.sensorDataAdapter = sensorDataAdapter;
        this.mvStore = mvStore;
    }

    @Override
    public MVMapSensorDataLogger<T> createSensorDataLogger() {
//        MVMap<SensorDataId, T> dataMap = mvStore.openMap("data", createMvMapBuilder());
        MVMap<Long, T> dataMap = createMvStoreMap();
        return new MVMapSensorDataLogger<>(dataMap);
    }

    public MVMap<Long, T> createMvStoreMap() {
        return mvStore.openMap("data", createMvMapBuilder());
    }

    private MVMap.Builder<Long, T> createMvMapBuilder() {
        MVMap.Builder<Long, T> builder = new MVMap.Builder<>();
//        builder.keyType(SensorDataIdDataType.INSTANCE);
        builder.valueType(new TelemetryPayloadType<>(sensorDataAdapter));
        return builder;
    }
}
