package io.github.tofpu.vertexlink.telemetry;

import io.github.tofpu.vertexlink.Constants;
import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.redis.RedisHandler;
import io.lettuce.core.api.StatefulRedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.tofpu.vertexlink.util.ConversionUtil.convertUUIDtoBytes;

public class TelemetryProcessor<T extends TelemetryPayload> implements Closeable {
    private static final int TELEMETRY_QUEUE_POLL_COUNT = 1_000;
    private static final Logger log = LoggerFactory.getLogger(TelemetryProcessor.class);

    private final GrpcDataAdapter<T> dataAdapter;
    private final TelemetryRegistry<T> telemetryRegistry;
    private final ExecutorService executorService;
    private final StatefulRedisConnection<byte[], byte[]> connection;

    public TelemetryProcessor(GrpcDataAdapter<T> grpcDataAdapter, RedisHandler redisHandler, TelemetryRegistry<T> telemetryRegistry) {
        this.dataAdapter = grpcDataAdapter;
        this.telemetryRegistry = telemetryRegistry;
        this.executorService = Executors.newSingleThreadExecutor();
        this.connection = redisHandler.getConnectionAsByteArray();
    }

    public void start() {
        executorService.submit(() -> {
            try {
                while (!executorService.isShutdown()) {
                    int processedCount = processTelemetryStack();
                    if (processedCount == 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            log.error("Thread interrupted", e);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error", e);
            }
        });
    }

    private int processTelemetryStack() {
        List<byte[]> list = connection.sync()
                .lpop(Constants.Redis.TELEMETRY_QUEUE_KEY, TELEMETRY_QUEUE_POLL_COUNT);
        int size = list.size();
        if (!list.isEmpty()) {
            log.info("Polled {} telemetries", size);
            list.forEach(this::processTelemetry);
        }
        return size;
    }

    private void processTelemetry(byte[] bytes) {
        T telemetry = dataAdapter.deserialize(bytes);
        NodeId nodeId = telemetry.nodeId();
        String telemetryType = telemetry.type();
        long telemetryId = telemetry.id();
        log.info("Processing '{}' telemetry from node {}: id={}", telemetryType, nodeId, telemetryId);

        byte[] nodeIdBytes = convertUUIDtoBytes(nodeId.uuid());

        T prevTelemetry = telemetryRegistry.get(nodeId);
        if (prevTelemetry == null || telemetryId > prevTelemetry.id()) {
            updateTelemetryRegistry(telemetryType, telemetryId, nodeId, nodeIdBytes, telemetry);
        } else {
            long newerTelemetryId = prevTelemetry.id();
            log.info("Dismissing '{}' telemetry as it is outdated. older_id={}, newer_id={}", telemetryType, telemetryId, newerTelemetryId);
        }
    }

    private void updateTelemetryRegistry(String telemetryType, long telemetryId, NodeId nodeId, byte[] nodeIdBytes, T telemetry) {
        log.info("Updating '{}' telemetry with id {} from node {}", telemetryType, telemetryId, nodeId);
        connection.sync().hset(
                Constants.Redis.LATEST_NODE_TELEMETRY,
                nodeIdBytes,
                dataAdapter.serialize(telemetry)
        );
        telemetryRegistry.register(
                nodeId,
                telemetry
        );
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }
}
