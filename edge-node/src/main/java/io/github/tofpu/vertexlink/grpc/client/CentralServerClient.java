package io.github.tofpu.vertexlink.grpc.client;

import com.google.protobuf.ByteString;
import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.protos.CentralServerServiceGrpc;
import io.github.tofpu.vertexlink.protos.NodeRegistrationRequest;
import io.github.tofpu.vertexlink.protos.NodeRegistrationResponse;
import io.github.tofpu.vertexlink.protos.TelemetryPayloadData;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.github.tofpu.vertexlink.util.ConversionUtil;
import io.github.tofpu.vertexlink.util.grpc.AbstractClient;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CentralServerClient<T extends TelemetryPayload> extends AbstractClient<
        CentralServerServiceGrpc.CentralServerServiceBlockingStub,
        CentralServerServiceGrpc.CentralServerServiceStub
        > {
    private static final Logger log = LoggerFactory.getLogger(CentralServerClient.class);
    private final GrpcDataAdapter<T> grpcDataAdapter;

    public CentralServerClient(String host, int port, GrpcDataAdapter<T> grpcDataAdapter) {
        super(host, port, CentralServerServiceGrpc::newBlockingStub, CentralServerServiceGrpc::newStub);
        this.grpcDataAdapter = grpcDataAdapter;
    }

    public NodeRegistrationResult registerEdgeNode(UUID nodeId, String host, int port) {
        log.info("Attempting to register this edge node ({}:{}) in the central server", host, port);
        NodeRegistrationRequest request = NodeRegistrationRequest.newBuilder()
                .setId(ByteString.copyFrom(ConversionUtil.convertUUIDtoBytes(nodeId)))
                .setHost(host)
                .setPort(port)
                .build();
        NodeRegistrationResponse response = blockingStub.registerEdgeNode(request);
        if (!response.getSuccess()) {
            log.info("Failed to register this node in the central server. ErrorType={}", response.getErrorType());
            return NodeRegistrationResult.FAILURE;
        }
        log.info("Successfully registered this node in the central server");
        return NodeRegistrationResult.SUCCESS;
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
