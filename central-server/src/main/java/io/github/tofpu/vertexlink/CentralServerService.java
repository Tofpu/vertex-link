package io.github.tofpu.vertexlink;

import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.grpc.RPCConnectionSettings;
import io.github.tofpu.vertexlink.grpc.VertexLinkService;
import io.github.tofpu.vertexlink.node.NodeClientRegistry;
import io.github.tofpu.vertexlink.node.NodeConnectionHandler;
import io.github.tofpu.vertexlink.redis.RedisConnectionSettings;
import io.github.tofpu.vertexlink.redis.RedisHandler;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.github.tofpu.vertexlink.telemetry.TelemetryProcessor;
import io.github.tofpu.vertexlink.telemetry.TelemetryRegistry;
import io.github.tofpu.vertexlink.util.grpc.SimpleServer;

import java.io.Closeable;
import java.io.IOException;

public class CentralServerService<T extends TelemetryPayload> implements Closeable {
    private final TelemetryProcessor<T> telemetryProcessor;
    private final SimpleServer<VertexLinkService<T>> server;

    public CentralServerService(
            RedisConnectionSettings redisConnectionSettings,
            RPCConnectionSettings rpcConnectionSettings,
            GrpcDataAdapter<T> grpcDataAdapter
    ) {
        var redisHandler = new RedisHandler(redisConnectionSettings.host(), redisConnectionSettings.port());
        var telemetryRegistry = new TelemetryRegistry<T>();
        this.telemetryProcessor = new TelemetryProcessor<>(
                grpcDataAdapter,
                redisHandler,
                telemetryRegistry
        );

        var nodeConnectionHandler = new NodeConnectionHandler(
                new NodeClientRegistry()
        );

        var vertexLinkService = new VertexLinkService<>(
                grpcDataAdapter,
                redisHandler,
                nodeConnectionHandler
        );
        this.server = new SimpleServer<>(
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
