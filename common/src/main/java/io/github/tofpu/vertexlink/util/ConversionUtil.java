package io.github.tofpu.vertexlink.util;

import io.github.tofpu.vertexlink.telemetry.NodeId;
import org.jspecify.annotations.NonNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ConversionUtil {
    public static byte @NonNull [] convertUUIDtoBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static NodeId convertToNodeId(byte[] bytes) {
        return NodeId.wrap(
                resolveToUUID(bytes)
        );
    }

    public static UUID resolveToUUID(byte[] bytes) {
        return UUID.nameUUIDFromBytes(bytes);
    }
}
