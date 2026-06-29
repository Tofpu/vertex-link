package io.github.tofpu.vertexlink;

import io.github.tofpu.vertexlink.grpc.ConnectionSettings;
import io.github.tofpu.vertexlink.grpc.GrpcDataAdapter;
import io.github.tofpu.vertexlink.grpc.NodeRegistrationResult;
import io.github.tofpu.vertexlink.grpc.VertexLinkClient;
import io.github.tofpu.vertexlink.grpc.server.VertexLinkNodeService;
import io.github.tofpu.vertexlink.logging.impl.MVMapSensorDataLogger;
import io.github.tofpu.vertexlink.logging.impl.MVStoreSensorDataLoggerFactory;
import io.github.tofpu.vertexlink.poller.TelemetryPoller;
import io.github.tofpu.vertexlink.telemetry.SensorDataAdapter;
import io.github.tofpu.vertexlink.telemetry.SensorDataIngestor;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import io.github.tofpu.vertexlink.util.grpc.SimpleServer;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.MVStoreTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EdgeNodeService<T extends TelemetryPayload> implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(EdgeNodeService.class);
    private static final String MVSTORE_FILE_PATH_IN_STRING = new File("data.db").getAbsolutePath();
    public static final int LOCAL_GRPC_PORT = 5001;

    private final UUID nodeId;
    private final VertexLinkClient<T> vertexLinkClient;
    private final SensorDataAdapter<T> sensorDataAdapter;
    private final MVStore mvStore;
    private final TelemetryPoller<T> telemetryPoller;
    private final SimpleServer<VertexLinkNodeService> server;

    public EdgeNodeService(
            UUID nodeId,
            ConnectionSettings connectionSettings,
            GrpcDataAdapter<T> grpcDataAdapter,
            SensorDataAdapter<T> sensorDataAdapter,
            SensorDataIngestor<T> sensorDataIngestor,
            TelemetryPoller.Settings telemetryPollerSettings
    ) {
        this.nodeId = nodeId;
        this.vertexLinkClient = new VertexLinkClient<>(
                connectionSettings.host(), connectionSettings.port(), grpcDataAdapter
        );
        this.sensorDataAdapter = sensorDataAdapter;

        this.mvStore = new MVStore.Builder()
                .fileName(MVSTORE_FILE_PATH_IN_STRING)
                .open();
        this.telemetryPoller = new TelemetryPoller<>(
                sensorDataIngestor, telemetryPollerSettings // 10 times per sec
        );

        this.server = new SimpleServer<>(
                LOCAL_GRPC_PORT, new VertexLinkNodeService()
        );
    }

    public void initialize() {
        log.info("Node id = {}", nodeId);
        startNodeRCPServer();
        tryToRegisterThisNodeToCentralServer();
        initializeTelemetryLogger();
    }

    private void startNodeRCPServer() {
        try {
            this.server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryToRegisterThisNodeToCentralServer() {
        // todo grab public address of this node
        NodeRegistrationResult nodeRegistrationResult = vertexLinkClient.registerEdgeNode(nodeId, "localhost", LOCAL_GRPC_PORT);
        if (!nodeRegistrationResult.success()) {
            throw new IllegalStateException("Failed to register the node with id " + nodeId);
        }
    }

    private void initializeTelemetryLogger() {
        var loggerFactory = new MVStoreSensorDataLoggerFactory<>(
                sensorDataAdapter, mvStore
        );
        MVMap<Long, T> mvMap = loggerFactory.createMvStoreMap();
        MVMapSensorDataLogger<T> sensorDataLogger = loggerFactory.createSensorDataLogger();

        telemetryPoller.start(telemetry -> {
            if (!uploadToRPC(telemetry)) {
                log.info("Writing telemetry to disk for later upload");
                sensorDataLogger.logData(telemetry);
                return;
            }

            tryToUploadPendingTelemetryIfPresent(sensorDataLogger, mvMap);
        });
    }

    private boolean uploadToRPC(T telemetry) {
        log.info("Uploading '{}' telemetry {}", telemetry.type(), telemetry);
        boolean uploaded = vertexLinkClient.uploadTelemetry(nodeId, telemetry);
        if (!uploaded) {
            log.info("Failed to upload '{}' telemetry {}", telemetry.type(), telemetry.id());
            return false;
        }
        log.info("Successfully uploaded '{}' telemetry {}", telemetry.type(), telemetry.id());
        return true;
    }

    private void tryToUploadPendingTelemetryIfPresent(MVMapSensorDataLogger<T> sensorDataLogger, MVMap<Long, T> mvMap) {
        List<T> removeList = new ArrayList<>();
        try {
            if (!sensorDataLogger.isEmpty()) {
                log.info("Found pending telemetries in disk! Attempting to upload them now....");
                for (T telemetry : sensorDataLogger) {
                    boolean uploaded = uploadToRPC(telemetry);
                    if (!uploaded) {
                        log.info("Encountered into upload failure. Aborting the process of uploading pending telemetries for now");
                        break;
                    } else {
                        log.info("Removing pending '{}' telemetry {}", telemetry.type(), telemetry.id());
                        removeList.add(telemetry);
                    }
                }
            }
        } finally {
            removeList.forEach(staleTelemetry -> mvMap.remove(staleTelemetry.id()));
        }
    }

    @Override
    public void close() {
        telemetryPoller.close();
        mvStore.close();

        log.info("---- INFO ----");
        MVStoreTool.info(MVSTORE_FILE_PATH_IN_STRING);
        log.info("---- DUMP ----");
        MVStoreTool.dump(MVSTORE_FILE_PATH_IN_STRING, false);
    }
}
