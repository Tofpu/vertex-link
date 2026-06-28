package io.github.tofpu.vertexlink.grpc;

import com.google.protobuf.ByteString;
import io.github.tofpu.vertexlink.protos.TelemetryPayloadData;
import io.github.tofpu.vertexlink.protos.VertexLinkServiceGrpc;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.github.tofpu.vertexlink.util.grpc.AbstractClient;
import io.grpc.StatusRuntimeException;

import java.util.UUID;

public class VertexLinkClient<T extends TelemetryPayload> extends AbstractClient<
        VertexLinkServiceGrpc.VertexLinkServiceBlockingStub,
        VertexLinkServiceGrpc.VertexLinkServiceStub
        > {
    private final GrpcDataAdapter<T> grpcDataAdapter;

    public VertexLinkClient(String host, int port, GrpcDataAdapter<T> grpcDataAdapter) {
        super(host, port, VertexLinkServiceGrpc::newBlockingStub, VertexLinkServiceGrpc::newStub);
        this.grpcDataAdapter = grpcDataAdapter;
    }

    /**
     * @return true if telemetry was successfully sent.
     */
    public boolean uploadTelemetry(UUID nodeId, T payload) {
        TelemetryPayloadData request = TelemetryPayloadData.newBuilder()
                .setNodeId(ByteString.copyFrom(nodeId.toString().getBytes()))
                .setType(payload.type())
                .setId(payload.id())
                .setData(ByteString.copyFrom(grpcDataAdapter.serialize(payload)))
                .build();
        try {
            //noinspection ResultOfMethodCallIgnored
            blockingStub.uploadTelemetry(request);
            return true;
        } catch (StatusRuntimeException e) {
            super.handleException(e);
            return false;
        }
    }
}
