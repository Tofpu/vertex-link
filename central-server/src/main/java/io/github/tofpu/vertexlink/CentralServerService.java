package io.github.tofpu.vertexlink;

import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.grpc.server.RPCConnectionSettings;
import io.github.tofpu.vertexlink.grpc.server.CentralServerServiceGrpc;
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
    private final SimpleServer<CentralServerServiceGrpc<T>> server;

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

        NodeClientRegistry nodeClientRegistry = new NodeClientRegistry();
        var nodeConnectionHandler = new NodeConnectionHandler(
                nodeClientRegistry
        );

        var vertexLinkService = new CentralServerServiceGrpc<>(
                grpcDataAdapter,
                redisHandler,
                nodeConnectionHandler,
                nodeClientRegistry
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
