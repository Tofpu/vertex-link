package io.github.tofpu.vertexlink.logging.impl;

import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import org.h2.mvstore.MVMap;

import java.util.Iterator;

public class MVMapSensorDataLogger<T extends TelemetryPayload> implements Iterable<T> {
    private final MVMap<Long, T> dataMap;

    public MVMapSensorDataLogger(MVMap<Long, T> dataMap) {
        this.dataMap = dataMap;
    }

    // todo when sending data to central server, send version + current size; that way we can know if it's behind or not
    //  and vice versa
    public void logData(T payload) {
        dataMap.put(payload.id(), payload);
    }

    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return dataMap.values().iterator();
    }
}
