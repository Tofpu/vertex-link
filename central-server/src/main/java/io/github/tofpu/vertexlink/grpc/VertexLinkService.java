package io.github.tofpu.vertexlink.grpc;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.github.tofpu.vertexlink.Constants;
import io.github.tofpu.vertexlink.protos.TelemetryPayloadData;
import io.github.tofpu.vertexlink.protos.VertexLinkServiceGrpc;
import io.github.tofpu.vertexlink.redis.RedisHandler;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.grpc.stub.StreamObserver;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;

import static io.github.tofpu.vertexlink.util.ConversionUtil.resolveToUUID;

public class VertexLinkService<T extends TelemetryPayload> extends VertexLinkServiceGrpc.VertexLinkServiceImplBase implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(VertexLinkService.class);
    private final GrpcDataAdapter<T> dataAdapter;
    private final StatefulRedisConnection<byte[], byte[]> connection;

    public VertexLinkService(GrpcDataAdapter<T> dataAdapter, RedisHandler redisHandler) {
        this.dataAdapter = dataAdapter;
        this.connection = redisHandler.getConnectionAsByteArray();
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
