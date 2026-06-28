package io.github.tofpu.temperatureproject;

import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;

import java.nio.ByteBuffer;

class TemperatureGrpcDataAdapter implements GrpcDataAdapter<TemperatureTelemetryPayload> {
    @Override
    public byte[] serialize(TemperatureTelemetryPayload payload) {
        return ByteBuffer.allocate(4).putInt(payload.value()).array();
    }
}
