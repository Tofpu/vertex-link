package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;

class ConfigSupplierImpl implements ConfigSupplier {
    private final ConfigService configService;

    ConfigSupplierImpl(ConfigService configService) {
        this.configService = configService;
    }

    @Override
    public Config obtainConfig() {
        return configService.config();
    }
}
