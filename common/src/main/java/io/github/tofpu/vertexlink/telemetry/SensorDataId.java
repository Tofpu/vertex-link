//package io.github.vertexlink.telemetry;
//
//import java.util.UUID;
//
//public record SensorDataId(
//        UUID uniqueId,
//        long timestampInMs
//) {
//
//    public static SensorDataId randomId() {
//        return new SensorDataId(UUID.randomUUID());
//    }
//
//    public SensorDataId(UUID uniqueId) {
//        this(uniqueId, System.currentTimeMillis());
//    }
//}
