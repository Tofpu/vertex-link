package io.github.tofpu.vertexlink.snowflakeidgenerator;

public interface SnowflakeGenerator {
    long nextId();

    static SnowflakeGenerator create(int workerId) {
        return new SnowflakeGeneratorImpl(workerId);
    }
}
