package io.github.tofpu.vertexlink.util;

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
}
