package io.github.tofpu.vertexlink;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.github.tofpu.vertexlink.config.ConfigurationListener;
import io.github.tofpu.vertexlink.grpc.client.CentralServerClient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InternalConfigurationListenerTest {
    @Test
    void givenConfigIsProvided_whenCallingConfigUpdate_thenConfigShouldBeSyncedAndDelegatedListenerShouldBeCalled() {
        UUID nodeId = UUID.randomUUID();
        Config newConfig = ConfigFactory.empty();

        CentralServerClient<?> centralServerClient = mock(CentralServerClient.class);
        ConfigurationListener delegateConfigListener = mock(ConfigurationListener.class);

        InternalConfigurationListener internalConfigListener = new InternalConfigurationListener(
                nodeId,
                centralServerClient,
                delegateConfigListener
        );
        internalConfigListener.onConfigurationUpdate(newConfig);

        verify(centralServerClient).syncConfig(eq(nodeId), eq(newConfig));
        verify(delegateConfigListener).onConfigurationUpdate(eq(newConfig));
    }
}
