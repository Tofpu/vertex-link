package io.github.tofpu.temperatureproject;

import com.typesafe.config.ConfigFactory;
import io.github.tofpu.vertexlink.EdgeNodeService;
import io.github.tofpu.vertexlink.grpc.server.ConnectionSettings;
import io.github.tofpu.vertexlink.poller.TelemetryPoller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher {
    private static final Logger log = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) throws InterruptedException {
        var nodeId = UUID.randomUUID();
        var connectionSettings = new ConnectionSettings("localhost", 5000);
        var edgeNodeService = new EdgeNodeService<>(
                nodeId,
                connectionSettings,
                new TemperatureGrpcDataAdapter(),
                new TemperatureDataAdapter(),
                new TemperatureSensorDataIngestor(nodeId),
                new TelemetryPoller.Settings(0),
                ConfigFactory.load(),
                newConfig -> log.info("ConfigListener. new config update: {}", newConfig)
        );

        edgeNodeService.initialize();

        AtomicBoolean running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            edgeNodeService.close();
        }));

        while (running.get()) {
            Thread.sleep(1000);
        }
    }
}
