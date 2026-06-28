package io.github.tofpu.vertexlink.telemetry;

import java.util.UUID;

public record NodeId(
        UUID uuid
) {
    public static NodeId wrap(UUID uuid) {
        return new NodeId(uuid);
    }
}
