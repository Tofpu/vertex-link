//package io.github.vertexlink.logging;
//
//import io.github.vertexlink.telemetry.SensorDataId;
//import org.h2.mvstore.WriteBuffer;
//import org.h2.mvstore.type.BasicDataType;
//
//import java.nio.ByteBuffer;
//import java.util.UUID;
//
//public class SensorDataIdDataType extends BasicDataType<SensorDataId> {
//    public static final SensorDataIdDataType INSTANCE = new SensorDataIdDataType();
//
//    private SensorDataIdDataType() {}
//
////    static final int UUID_STRING_LENGTH_SIZE = 36;
//
//    @Override
//    public int getMemory(SensorDataId id) {
//        var idUuid = 16; // id.uniqueId()
//        var idTimestamp = 8; // id.timestampInMs();
//        return idUuid + idTimestamp;
//    }
//
//    @Override
//    public boolean isMemoryEstimationAllowed() {
//        return true;
//    }
//
//    @Override
//    public void write(WriteBuffer buff, SensorDataId id) {
////        buff.putStringData(id.uniqueId().toString(), UUID_STRING_LENGTH_SIZE);
//        buff.putLong(id.uniqueId().getMostSignificantBits());
//        buff.putLong(id.uniqueId().getLeastSignificantBits());
//        buff.putLong(id.timestampInMs());
//    }
//
//    @Override
//    public SensorDataId read(ByteBuffer buff) {
//        return readSensorDataId(buff);
//    }
//
//    private static SensorDataId readSensorDataId(ByteBuffer buff) {
//        UUID uuid = readUUID(buff);
//        long timestampInMs = buff.getLong();
//        return new SensorDataId(
//                uuid,
//                timestampInMs
//        );
//    }
//
//    private static UUID readUUID(ByteBuffer buff) {
//        long mostSignificantBits = buff.getLong();
//        long leastSignificantBits = buff.getLong();
//        return new UUID(
//                mostSignificantBits,
//                leastSignificantBits
//        );
//    }
//
//    @Override
//    public SensorDataId[] createStorage(int size) {
//        return new SensorDataId[size];
//    }
//
//    @Override
//    public int compare(SensorDataId a, SensorDataId b) {
//        return Long.compare(a.timestampInMs(), b.timestampInMs());
//    }
//}
