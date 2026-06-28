package io.github.tofpu.vertexlink.grpc;

import com.google.protobuf.ByteString;
import io.github.tofpu.vertexlink.protos.TelemetryPayloadData;
import io.github.tofpu.vertexlink.protos.VertexLinkServiceGrpc;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.UUID;

public class VertexLinkClient<T extends TelemetryPayload> {
    private static final Logger log = LoggerFactory.getLogger(VertexLinkClient.class);
    private final GrpcDataAdapter<T> grpcDataAdapter;

    private final ManagedChannel channel;
    private final VertexLinkServiceGrpc.VertexLinkServiceBlockingStub blockingStub;
    private final VertexLinkServiceGrpc.VertexLinkServiceStub asyncStub;

    public VertexLinkClient(String host, int port, GrpcDataAdapter<T> grpcDataAdapter) {
        this(grpcDataAdapter, ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    /**
     * Construct client for accessing RouteGuide server using the existing channel.
     */
    public VertexLinkClient(GrpcDataAdapter<T> grpcDataAdapter, ManagedChannelBuilder<?> channelBuilder) {
        this.grpcDataAdapter = grpcDataAdapter;
        this.channel = channelBuilder.build();
        this.blockingStub = VertexLinkServiceGrpc.newBlockingStub(channel);
        this.asyncStub = VertexLinkServiceGrpc.newStub(channel);
    }

    /**
     * @return true if telemetry was successfully sent.
     */
    public boolean uploadTelemetry(UUID nodeId, T payload) {
        TelemetryPayloadData request = TelemetryPayloadData.newBuilder()
                .setNodeId(ByteString.copyFrom(nodeId.toString().getBytes()))
                .setType(payload.type())
//                .setTimestamp(payload.id().timestampInMs())
                .setId(payload.id())
                .setData(ByteString.copyFrom(grpcDataAdapter.serialize(payload)))
                .build();
        try {
            //noinspection ResultOfMethodCallIgnored
            blockingStub.uploadTelemetry(request);
            return true;
        } catch (StatusRuntimeException e) {
//            throw new RuntimeException(e);
//            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            if (isConnectionException(e)) {
                log.error("Failed to upload telemetry: Cannot reach the server (Network/Connection issue).");
            } else {
                log.info("Failed to upload telemetry. RPC failed: {}", e.getStatus());
            }
            return false;
        }
    }

    private boolean isConnectionException(StatusRuntimeException e) {
        // Connection drops and timeouts almost always fall under UNAVAILABLE
        if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause instanceof ConnectException) {
                    return true;
                }
                cause = cause.getCause(); // Move down the causal chain
            }
        }
        return false;
    }
}
