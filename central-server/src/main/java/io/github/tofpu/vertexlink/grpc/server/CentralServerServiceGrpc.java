package io.github.tofpu.vertexlink.grpc.server;

import com.google.protobuf.Empty;
import io.github.tofpu.vertexlink.Constants;
import io.github.tofpu.vertexlink.config.serializer.ConfigSerializer;
import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.grpc.client.EdgeNodeClient;
import io.github.tofpu.vertexlink.node.NodeClientRegistry;
import io.github.tofpu.vertexlink.node.NodeConnectionHandler;
import io.github.tofpu.vertexlink.node.NodeRegistrationResult;
import io.github.tofpu.vertexlink.protos.*;
import io.github.tofpu.vertexlink.redis.RedisHandler;
import io.github.tofpu.vertexlink.telemetry.NodeId;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.github.tofpu.vertexlink.util.ConversionUtil;
import io.grpc.stub.StreamObserver;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import static io.github.tofpu.vertexlink.util.ConversionUtil.resolveToUUID;

public class CentralServerServiceGrpc<T extends TelemetryPayload> extends io.github.tofpu.vertexlink.protos.CentralServerServiceGrpc.CentralServerServiceImplBase implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(CentralServerServiceGrpc.class);
    private final GrpcDataAdapter<T> dataAdapter;
    private final StatefulRedisConnection<byte[], byte[]> connection;

    private final NodeConnectionHandler nodeConnectionHandler;
    private final NodeClientRegistry nodeClientRegistry;

    public CentralServerServiceGrpc(GrpcDataAdapter<T> dataAdapter, RedisHandler redisHandler, NodeConnectionHandler nodeConnectionHandler, NodeClientRegistry nodeClientRegistry) {
        this.dataAdapter = dataAdapter;
        this.connection = redisHandler.getConnectionAsByteArray();
        this.nodeConnectionHandler = nodeConnectionHandler;
        this.nodeClientRegistry = nodeClientRegistry;
    }

    @Override
    public void registerEdgeNode(NodeRegistrationRequest request, StreamObserver<NodeRegistrationResponse> responseObserver) {
        log.info("Received request: {}", request);
        NodeId nodeId = ConversionUtil.convertToNodeId(request.getId().toByteArray());
        NodeRegistrationResult registrationResult = nodeConnectionHandler.validateAndRegisterNode(
                nodeId,
                request.getHost(),
                request.getPort(),
                ConfigSerializer.serializer().deserialize(
                        request.getRawConfig()
                )
        );

        NodeRegistrationResponse.Builder responseBuilder = NodeRegistrationResponse.newBuilder()
                .setSuccess(registrationResult.success());

        if (!registrationResult.success()) {
            ErrorType errorType = resolveErrorType(registrationResult);
            responseBuilder = responseBuilder.setErrorType(errorType);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private static @NonNull ErrorType resolveErrorType(NodeRegistrationResult registrationResult) {
        ErrorType errorType;
        if (registrationResult == NodeRegistrationResult.UNREACHABLE_ADDRESS) {
            errorType = ErrorType.UNREACHABLE_ADDRESS;
        } else if (registrationResult == NodeRegistrationResult.CLIENT_ALREADY_EXISTS) {
            errorType = ErrorType.CLIENT_ALREADY_EXISTS;
        } else if (registrationResult == NodeRegistrationResult.UNFAMILIAR_ERROR) {
            errorType = ErrorType.UNFAMILIAR_ERROR;
        } else {
            throw new IllegalStateException("Unknown registration result: " + registrationResult);
        }
        return errorType;
    }

    @Override
    public void syncConfig(ConfigurationSynchronizationRequest request, StreamObserver<ConfigurationSynchronizationResponse> responseObserver) {
        UUID nodeId = resolveToUUID(request.getNodeId().toByteArray());
        EdgeNodeClient node = nodeClientRegistry.getClient(
                NodeId.wrap(nodeId)
        );

        ConfigurationSynchronizationResponse.Builder responseBuilder = ConfigurationSynchronizationResponse.newBuilder();
        if (node == null) {
            responseBuilder.setSuccess(false).setErrorType(ConfigSyncErrorType.UNFAMILIAR_NODE);
        } else {
            node.updateConfig(
                    ConfigSerializer.serializer().deserialize(request.getRawConfig())
            );
            responseBuilder.setSuccess(true);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void uploadTelemetry(TelemetryPayloadData request, StreamObserver<Empty> responseObserver) {
        UUID nodeId = resolveToUUID(request.getNodeId().toByteArray());
        T data = dataAdapter.deserialize(request);
        log.info("[{}] Received Telemetry Payload: {}", nodeId, data);

        RedisAsyncCommands<byte[], byte[]> async = connection.async();
        async.lpush(Constants.Redis.TELEMETRY_QUEUE_KEY, request.toByteArray());

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }
}
