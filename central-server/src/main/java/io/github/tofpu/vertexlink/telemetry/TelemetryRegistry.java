package io.github.tofpu.vertexlink.telemetry;

import java.util.HashMap;
import java.util.Map;

public class TelemetryRegistry<T extends TelemetryPayload> {
    private final Map<NodeId, T> map = new HashMap<>();

    public void register(NodeId id, T telemetry) {
        ensureTelemetryIsNewer(id, telemetry);
        map.put(id, telemetry);
    }

    private void ensureTelemetryIsNewer(NodeId id, T telemetry) {
        T prevTelemetry = map.get(id);
        if (prevTelemetry != null && prevTelemetry.id() > telemetry.id()) {
            throw new IllegalStateException("Cannot overwrite a new telemetry with an older telemetry.");
        }
    }

    public T get(NodeId id) {
        return map.get(id);
    }

    public void remove(NodeId id) {
        map.remove(id);
    }
}
