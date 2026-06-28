package io.github.tofpu.vertexlink.snowflakeidgenerator;

import java.nio.ByteBuffer;
import java.time.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

class SnowflakeGeneratorImpl implements SnowflakeGenerator {

    private static final Instant CUSTOM_EPOCH = Instant.parse("2026-01-01T00:00:00.00Z");
    private static final int WORKER_ID_BIT_SHIFT = 10;
    private static final int LOCAL_MACHINE_BIT_SHIFT = 12;

    public static void main(String[] args) throws InterruptedException {
//        System.out.println(Integer.bitCount(1024)); // used 11 bits
//        System.out.println(Integer.bitCount(1023)); // used 10 bits
//        System.out.println(Integer.bitCount(0)); // used 0 bits
//        System.out.println(Integer.bitCount(1)); // used 0 bits
        System.out.println(Long.MAX_VALUE);
        System.out.println(Long.MAX_VALUE + 1);
        System.out.println((1 << 63));
//        new SnowflakeGeneratorImpl(1000).nextId();

        System.out.println(4096 + 1 & 4095);

//        Instant now = Instant.parse(LocalDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
//        Instant now = ZonedDateTime.now().toInstant();
        Instant now = Instant.parse("2026-01-01T00:00:00.00Z");

        System.out.println(Duration.between(now, Instant.now()).getSeconds()); // 0
        Thread.sleep(1000);
        System.out.println(Duration.between(now, Instant.now()).getSeconds()); // 1
    }

    public static int validateBitWidth(int value, int maxBits) {
        if (maxBits < 1 || maxBits > 31) {
            throw new IllegalArgumentException("Bit width must be between 1 and 31");
        }

        // 1 << maxBits creates the first value that is TOO LARGE for that bit width
        // e.g., 1 << 8 is 256 (which requires 9 bits)
        if (value < 0 || value >= (1 << maxBits)) {
            throw new IllegalArgumentException("Value exceeds the allowed " + maxBits + " bits.");
        }
        return value;
    }

    public static long validateBitWidth(long value, long maxBits) {
        if (maxBits < 1 || maxBits > 63) {
            throw new IllegalArgumentException("Bit width must be between 1 and 63");
        }

        // 1 << maxBits creates the first value that is TOO LARGE for that bit width
        // e.g., 1 << 8 is 256 (which requires 9 bits)
        if (value < 0 || value >= (1L << maxBits)) {
            throw new IllegalArgumentException("Value exceeds the allowed " + maxBits + " bits.");
        }
        return value;
    }

    private final int workerId;

    private volatile long lastTimestamp = -1L;
    private final AtomicInteger sequence = new AtomicInteger(0);

    public SnowflakeGeneratorImpl(int workerId) {
        this.workerId = validateBitWidth(workerId, 10);
    }

    @Override
    public long nextId() {
        long timestamp = Duration.between(CUSTOM_EPOCH, Instant.now()).toMillis();
        validateBitWidth(timestamp, 41);

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards");
        } else if (timestamp == lastTimestamp) {
            // increments counter because we obtained the same timestamp as the previous id generator
            int result = sequence.updateAndGet(operand -> {
                return (operand + 1) & 4095; // 12-bit mask
            });
            if (result == 0) {
                // Sequence overflow, wait for next millisecond
                timestamp = blockUntilNextMillis(lastTimestamp);
            }
        } else {
            // reset counter because we obtained a fresh timestamp
            sequence.set(0);
        }
        lastTimestamp = timestamp;

        // long reserves a bit for the positive/negative sign, so, out of 64 bits, we have 63 bits
        // current time millis uses 41 bits (11001111100000001011000010010101000100011), 22 bits remaining
        // node id uses 10 bits (1024 possible combinations), 12 bits remaining
        // counter uses 12 bits (4096 possible combinations)

        long id = timestamp;

        id = id << WORKER_ID_BIT_SHIFT; // push id bits to the left 10 times
        id = id | workerId; // a max of 1024 possible combinations, which uses 10 bits max; integrates node id to the id

        id = id << LOCAL_MACHINE_BIT_SHIFT; // push id bits to the left 13 times
        id = id | sequence.get(); // a max of 4096 possible combinations, which uses 12 bits max; incremental counter if prev time millis matched (and perhaps node id matched as well...?)

        System.out.println("id = " + id);
        return id;
    }

    private long blockUntilNextMillis(long lastTimestamp) {
        long timestamp = Duration.between(CUSTOM_EPOCH, Instant.now()).toMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = Duration.between(CUSTOM_EPOCH, Instant.now()).toMillis();
        }
        return timestamp;
    }

//    @Override
//    public long nextId() {
//        // a long is 64-bit
////        long id = 0;
//
//        // 0110 = 6
//        // 0101 = 5
//        // OR
//        // 0111 = 7 (it is true/1 if either input is true)
//
//        // 0110 = 6
//        // 0101 = 5
//        // AND
//        // 0100 = 4 (it is true/1 if both input is true)
//
//        // 0110 = 6
//        // 0101 = 5
//        // XOR (exclusive disjunction) (it is true/1 if either input is true but false/0 if both inputs match)
//        // 0011 = 3
//
//        // 0000 0110 = 6
//        // 0000 0101 = 5
//        // 00000 01000 = 8
//        // NOT (bitwise complement)
//        // 1111 1001 -> 0000 0110 + 1 -> 0000 0111 = -7
//        // 1111 1010 -> 0000 0101 + 1 -> 0000 0110 = -6
//        // 11111 10111 -> 00000 01000 + 1 -> 00000 01001 = -9
//
////        System.out.println(~6);
////        System.out.println(~5);
////        System.out.println(~8);
//
////        System.out.println(1 << 3);
////        System.out.println(toPrettyBinaryRepresentation(ByteBuffer.allocate(64).putLong(Long.MAX_VALUE + 1)));
//        System.out.println(toPrettyBinaryRepresentation(ByteBuffer.allocate(64)));
////        System.out.println(toPrettyBinaryRepresentation(ByteBuffer.putLong(System.currentTimeMillis())));
////        long ms = System.currentTimeMillis();
//        long ms = Duration.between(START_INSTANT, Instant.now()).toMillis();
//        validateBitWidth(ms, 41);
//
//        System.out.println(Long.toBinaryString(ms));
////        System.out.println(Long.toBinaryString(1000));
////        System.out.println(Long.toBinaryString(System.currentTimeMillis() << 10));
////        System.out.println(Long.toBinaryString((System.currentTimeMillis() << 10) | 1000));
//        System.out.println(Long.toBinaryString(1023));
//        System.out.println(Long.toBinaryString((ms << 10) | 1023));
////        System.out.println(Long.toBinaryString(8000));
////        System.out.println(Long.toBinaryString((((System.currentTimeMillis() << 10) | 1000) << 13) | 8000));
//        System.out.println(Long.toBinaryString(8191));
//        System.out.println(Long.toBinaryString((((ms << 10) | 1000) << 13) | 8191));
//
//        // long reserves a bit for the positive/negative sign, so, out of 64 bits, we have 63 bits
//        // current time millis uses 41 bits (11001111100000001011000010010101000100011), 22 bits remaining
//        // node id uses 10 bits (1024 possible combinations), 12 bits remaining
//        // counter uses 12 bits (4096 possible combinations)
//
//        long id = ms;
//
//        id = id << 10; // push id bits to the left 10 times
//        id = id | workerId; // a max of 1024 possible combinations, which uses 10 bits max; integrates node id to the id
//
//        id = id << 12; // push id bits to the left 13 times
//        id = id | 4095; // a max of 4096 possible combinations, which uses 12 bits max; incremental counter if prev time millis matched (and perhaps node id matched as well...?)
//
//        System.out.println("id = " + id);
//
//        return id;
//    }

    public String toPrettyBinaryRepresentation(ByteBuffer byteBuffer) {
//        System.out.println("test: " + byteBuffer.getLong());
        return Arrays.toString(byteBuffer.array()).replace("[", "").replace(", ", "").replace("]", "");
    }
}
