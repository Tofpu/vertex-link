package io.github.tofpu.temperatureproject;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.protos.TelemetryPayloadData;
import io.github.tofpu.vertexlink.util.ConversionUtil;

import java.nio.ByteBuffer;
import java.util.UUID;

import static io.github.tofpu.vertexlink.util.ConversionUtil.convertToNodeId;

class TelemetryPayloadGrpcDataAdapter implements GrpcDataAdapter<TemperatureTelemetryPayload> {
    @Override
    public TemperatureTelemetryPayload deserialize(TelemetryPayloadData data) {
        return new TemperatureTelemetryPayload(
                convertToNodeId(data.getNodeId().toByteArray()),
                data.getId(),
                data.getData().asReadOnlyByteBuffer().getInt()
        );
    }

    @Override
    public TemperatureTelemetryPayload deserialize(byte[] data) {
        TelemetryPayloadData telemetryPayloadData;
        try {
            telemetryPayloadData = TelemetryPayloadData.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return deserialize(telemetryPayloadData);
    }

    // todo this code is duplicated in client's app internal code
    @Override
    public byte[] serialize(TemperatureTelemetryPayload data) {
        UUID nodeId = data.nodeId().uuid();
        return TelemetryPayloadData.newBuilder()
                .setNodeId(ByteString.copyFrom(ConversionUtil.convertUUIDtoBytes(nodeId)))
                .setId(data.id())
                .setType(data.type())
                .setData(ByteString.copyFrom(ByteBuffer.allocate(32).putInt(data.value())))
                .build().toByteArray();
    }
}
