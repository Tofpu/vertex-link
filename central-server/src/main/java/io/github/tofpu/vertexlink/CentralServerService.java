package io.github.tofpu.vertexlink;

import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.grpc.RPCConnectionSettings;
import io.github.tofpu.vertexlink.grpc.VertexLinkServer;
import io.github.tofpu.vertexlink.grpc.VertexLinkService;
import io.github.tofpu.vertexlink.redis.RedisConnectionSettings;
import io.github.tofpu.vertexlink.redis.RedisHandler;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.github.tofpu.vertexlink.telemetry.TelemetryProcessor;
import io.github.tofpu.vertexlink.telemetry.TelemetryRegistry;

import java.io.Closeable;
import java.io.IOException;

public class CentralServerService<T extends TelemetryPayload> implements Closeable {
    private final TelemetryProcessor<T> telemetryProcessor;
    private final VertexLinkServer server;

    public CentralServerService(
            RedisConnectionSettings redisConnectionSettings,
            RPCConnectionSettings rpcConnectionSettings,
            GrpcDataAdapter<T> grpcDataAdapter
    ) {
        RedisHandler redisHandler = new RedisHandler(redisConnectionSettings.host(), redisConnectionSettings.port());
        TelemetryRegistry<T> telemetryRegistry = new TelemetryRegistry<>();
        this.telemetryProcessor = new TelemetryProcessor<>(
                grpcDataAdapter,
                redisHandler,
                telemetryRegistry
        );

        VertexLinkService<T> vertexLinkService = new VertexLinkService<>(
                grpcDataAdapter,
                redisHandler
        );
        this.server = new VertexLinkServer(
                rpcConnectionSettings.port(),
                vertexLinkService
        );
    }

    public void initialize() throws IOException {
        telemetryProcessor.start();
        server.start();
    }

    @Override
    public void close() throws IOException {
        telemetryProcessor.close();
    }
}
