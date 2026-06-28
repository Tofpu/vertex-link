package io.github.tofpu.vertexlink.logging;

import io.github.tofpu.vertexlink.telemetry.SensorDataAdapter;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.DataType;

import java.nio.ByteBuffer;

public class TelemetryPayloadType<T extends TelemetryPayload> implements DataType<T> {
    private final SensorDataAdapter<T> sensorDataAdapter;

    public TelemetryPayloadType(SensorDataAdapter<T> sensorDataAdapter) {
        this.sensorDataAdapter = sensorDataAdapter;
    }

    @Override
    public int compare(T a, T b) {
        throw new UnsupportedOperationException("Only required for keys");
    }

    @Override
    public int binarySearch(T key, Object storage, int size, int initialGuess) {
        throw new UnsupportedOperationException("Only required for keys");
    }

    @Override
    public int getMemory(T obj) {
        return sensorDataAdapter.calculateMemory(obj);
    }

    @Override
    public boolean isMemoryEstimationAllowed() {
        return sensorDataAdapter.isMemoryEstimationAllowed();
    }

    @Override
    public void write(WriteBuffer buff, T obj) {
        sensorDataAdapter.write(buff, obj);
    }

    @Override
    public void write(WriteBuffer buff, Object storage, int len) {
        // boiler plate for most cases
        // todo fix
        if (storage instanceof TelemetryPayload[] storageAray) {
            for (int i = 0; i < len; i++) {
                write(buff, (T) storageAray[i]);
            }
        } else {
            write(buff, (T) storage);
        }
    }

    @Override
    public T read(ByteBuffer buff) {
        return sensorDataAdapter.read(buff);
    }

    @Override
    public void read(ByteBuffer buff, Object storage, int len) {
        // boiler plate for most cases
        // todo fix
        if (storage instanceof TelemetryPayload[] storageAray) {
            for (int i = 0; i < len; i++) {
                storageAray[i] = read(buff);
            }
        } else {
            throw new UnsupportedOperationException("Expected storage parameter of DataType#read to be TemperatureTelemetryPayload[], but it was " + storage.getClass());
        }
    }

    @Override
    public T[] createStorage(int size) {
        return sensorDataAdapter.createStorage(size);
    }
}
