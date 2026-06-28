package io.github.tofpu.temperatureproject;

import io.github.tofpu.vertexlink.CentralServerService;
import io.github.tofpu.vertexlink.grpc.RPCConnectionSettings;
import io.github.tofpu.vertexlink.redis.RedisConnectionSettings;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Launcher {
    public static void main(String[] args) throws InterruptedException {
        var centralServerService = new CentralServerService<>(
                new RedisConnectionSettings("192.168.1.8"),
                new RPCConnectionSettings(5000),
                new TelemetryPayloadGrpcDataAdapter()
        );
        try {
            centralServerService.initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AtomicBoolean running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running.set(false);
            try {
                centralServerService.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        while (running.get()) {
            Thread.sleep(1000);
        }
    }
}
