package io.github.tofpu.vertexlink.poller;

import io.github.tofpu.vertexlink.telemetry.SensorDataIngestor;
import io.github.tofpu.vertexlink.telemetry.TelemetryPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class TelemetryPoller<T extends TelemetryPayload> implements Closeable {
    private static final AtomicInteger THREAD_INCREMENTER = new AtomicInteger(0);
    private static final Logger log = LoggerFactory.getLogger(TelemetryPoller.class);

    private final SensorDataIngestor<T> sensorDataIngestor;
    private final TelemetryPoller.Settings settings;
    private final ExecutorService executorService;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public TelemetryPoller(SensorDataIngestor<T> sensorDataIngestor, Settings settings) {
        this.sensorDataIngestor = sensorDataIngestor;
        this.settings = settings;
        this.executorService = Executors.newSingleThreadExecutor(r -> {
            int id = THREAD_INCREMENTER.incrementAndGet();
            String name = "telemetry-poller-%d".formatted(id);
            return new Thread(r, name);
        });
    }

    public void start(Consumer<T> consumer) {
        if (started.get()) {
            throw new RuntimeException("TelemetryPoller already started");
        }
        executorService.submit(() -> {
            while (!executorService.isShutdown()) {
                try {
                    T data = sensorDataIngestor.readCurrentData();
                    consumer.accept(data);

                    try {
                        if (settings.intervalInMs > 0) {
                            Thread.sleep(settings.intervalInMs);
                        }
                    } catch (InterruptedException e) {
                        log.error("Thread interrupted", e);
                    }
                } catch (Exception e) {
                    log.error("Error occurred", e);
                }
            }
        });
        started.set(true);
    }

    @Override
    public void close() {
        // todo check online how to properly close executor service
        executorService.close();
    }

    public record Settings(
            int intervalInMs
    ) {}
}
