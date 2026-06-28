package io.github.tofpu.vertexlink.redis;

import io.lettuce.core.RedisURI;

public record RedisConnectionSettings(
        String host,
        int port
) {
    public RedisConnectionSettings(String host) {
        this(host, RedisURI.DEFAULT_REDIS_PORT);
    }
}
