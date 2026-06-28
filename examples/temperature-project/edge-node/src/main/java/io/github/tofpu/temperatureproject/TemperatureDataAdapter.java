package io.github.tofpu.temperatureproject;

import io.github.tofpu.vertexlink.telemetry.NodeId;
import io.github.tofpu.vertexlink.telemetry.SensorDataAdapter;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

import java.nio.ByteBuffer;
import java.util.UUID;

class TemperatureDataAdapter implements SensorDataAdapter<TemperatureTelemetryPayload> {
    static final int UUID_STRING_LENGTH_SIZE = 36;

    @Override
    public int calculateMemory(TemperatureTelemetryPayload obj) {
        @SuppressWarnings("TooBroadScope") var nodeIdValue = UUID_STRING_LENGTH_SIZE;
        var telemetryValue = 4; // obj.value();
//        var idValue = SensorDataIdDataType.INSTANCE.getMemory(obj.id()); // obj.id();
        var idValue = 8; // obj.id();
        return nodeIdValue + telemetryValue + idValue;
    }

    @Override
    public void write(WriteBuffer buff, TemperatureTelemetryPayload payload) {
        buff.putStringData(payload.nodeId().uuid().toString(), UUID_STRING_LENGTH_SIZE);
        buff.putLong(payload.id());
        buff.putInt(payload.value());
//        buff.putLong(payload.id().timestampInMs());
    }

    @Override
    public TemperatureTelemetryPayload read(ByteBuffer buff) {
        UUID nodeId = resolveUUIDFromString(buff);
        long id = buff.getLong();
        int telemetryPayload = buff.getInt();
        return new TemperatureTelemetryPayload(
                NodeId.wrap(nodeId),
                id,
                telemetryPayload
        );
    }

    private static UUID resolveUUIDFromString(ByteBuffer buff) {
        String string = DataUtils.readString(buff, UUID_STRING_LENGTH_SIZE);
        return UUID.fromString(string);
    }

    @Override
    public TemperatureTelemetryPayload[] createStorage(int size) {
        return new TemperatureTelemetryPayload[size];
    }
}
