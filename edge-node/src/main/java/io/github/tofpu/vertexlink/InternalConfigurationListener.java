package io.github.tofpu.vertexlink;

import com.typesafe.config.Config;
import io.github.tofpu.vertexlink.config.ConfigurationListener;
import io.github.tofpu.vertexlink.config.ForwardingConfigurationListener;
import io.github.tofpu.vertexlink.grpc.client.CentralServerClient;

import java.util.UUID;

public class InternalConfigurationListener extends ForwardingConfigurationListener {
    private final UUID nodeId;
    private final CentralServerClient<?> centralServerClient;

    public InternalConfigurationListener(UUID nodeId, CentralServerClient<?> centralServerClient, ConfigurationListener delegate) {
        super(delegate);
        this.nodeId = nodeId;
        this.centralServerClient = centralServerClient;
    }

    @Override
    public void onConfigurationUpdate(Config newConfig) {
        centralServerClient.syncConfig(nodeId, newConfig);
        super.onConfigurationUpdate(newConfig);
    }
}
