package io.github.tofpu.vertexlink.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

public class RedisHandler {
    private final RedisClient client;

    public RedisHandler(String host, int port) {
        this.client = RedisClient.create("redis://%s:%s".formatted(host, port));
    }

    public StatefulRedisConnection<String, String> getConnectionAsString() {
        return getConnection(String.class);
    }

    public StatefulRedisConnection<byte[], byte[]> getConnectionAsByteArray() {
        return getConnection(byte[].class);
    }

    <T> StatefulRedisConnection<T, T> getConnection(Class<T> codecType) {
        if (codecType == String.class) {
            return (StatefulRedisConnection<T, T>) client.connect(StringCodec.UTF8);
        } else if (codecType == byte[].class) {
            return (StatefulRedisConnection<T, T>) client.connect(ByteArrayCodec.INSTANCE);
        }
        throw new RuntimeException("Unknown codec type: " + codecType);
    }

    public StatefulRedisPubSubConnection<String, String> getPubSubConnection() {
        return client.connectPubSub();
    }
}
