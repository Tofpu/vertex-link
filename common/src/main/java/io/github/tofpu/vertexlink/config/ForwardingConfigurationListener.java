package io.github.tofpu.vertexlink.config;

import com.typesafe.config.Config;

public class ForwardingConfigurationListener implements ConfigurationListener {
    private final ConfigurationListener delegate;

    public ForwardingConfigurationListener(ConfigurationListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onConfigurationUpdate(Config newConfig) {
        delegate.onConfigurationUpdate(newConfig);
    }
}
