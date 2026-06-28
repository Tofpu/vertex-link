package io.github.tofpu.temperatureproject;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.protos.TelemetryPayloadData;
import io.github.tofpu.vertexlink.telemetry.NodeId;
import io.github.tofpu.vertexlink.util.ConversionUtil;

import java.nio.ByteBuffer;
import java.util.UUID;

class TelemetryPayloadGrpcDataAdapter implements GrpcDataAdapter<TemperatureTelemetryPayload> {
    @Override
    public TemperatureTelemetryPayload deserialize(TelemetryPayloadData data) {
//        SensorDataId sensorDataId = new SensorDataId(
////                        resolveToUUID(data.getTimestamp()),
//                UUID.randomUUID(),
//                data.getTimestamp()
//        );
        return new TemperatureTelemetryPayload(
                resolveToNodeId(data.getNodeId()),
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

    // todo this code is duplicated in client's app internal code

    NodeId resolveToNodeId(ByteString byteString) {
        return NodeId.wrap(
                resolveToUUID(byteString)
        );
    }

    // todo this code is duplicated in client's app internal code
    private UUID resolveToUUID(ByteString nodeId) {
        return UUID.nameUUIDFromBytes(nodeId.toByteArray());
    }
}
