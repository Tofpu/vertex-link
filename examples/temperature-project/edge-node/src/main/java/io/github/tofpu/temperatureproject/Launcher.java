package io.github.tofpu.temperatureproject;

import io.github.tofpu.vertexlink.EdgeNodeService;
import io.github.tofpu.vertexlink.grpc.ConnectionSettings;
import io.github.tofpu.vertexlink.poller.TelemetryPoller;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher {
    public static void main(String[] args) throws InterruptedException {
        var nodeId = UUID.randomUUID();
        var connectionSettings = new ConnectionSettings("localhost", 5000);
        var edgeNodeService = new EdgeNodeService<>(
                nodeId,
                connectionSettings,
                new TemperatureGrpcDataAdapter(),
                new TemperatureDataAdapter(),
                new TemperatureSensorDataIngestor(nodeId),
                new TelemetryPoller.Settings(0)
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
