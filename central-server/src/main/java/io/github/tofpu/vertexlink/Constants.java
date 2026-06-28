package io.github.tofpu.vertexlink;

import java.nio.charset.StandardCharsets;

public interface Constants {
    interface Redis {
        byte[] TELEMETRY_QUEUE_KEY = "telemetry:queue".getBytes(StandardCharsets.UTF_8);
        byte[] LATEST_NODE_TELEMETRY = "telemetry:latest".getBytes(StandardCharsets.UTF_8);
    }
}
