package io.github.tofpu.vertexlink;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import io.github.tofpu.vertexlink.config.ConfigService;
import io.github.tofpu.vertexlink.config.ConfigurationListener;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class ConfigServiceTest {
    @Test
    void givenEmptyConfigIsProvided_whenConstructingConfigService_thenExceptionShouldBeThrown() {
        Config emptyConfig = ConfigFactory.empty();

        assertThatExceptionOfType(ConfigException.ValidationFailed.class)
                .isThrownBy(() -> new ConfigService(
                        emptyConfig,
                        ConfigurationListener.IDENTITY
                ));
    }

    @Test
    void givenEmptyConfigIsProvided_whenUpdatingConfig_thenExceptionShouldBeThrown() {
        Config validConfig = ConfigFactory.load();
        ConfigService configService = new ConfigService(
                validConfig,
                ConfigurationListener.IDENTITY
        );

        Config emptyConfig = ConfigFactory.empty();
        assertThatExceptionOfType(ConfigException.ValidationFailed.class)
                .isThrownBy(() -> configService.updateConfig(emptyConfig));
    }

    @Test
    void givenValidConfigIsUpdated_whenUpdatingConfig_thenConfigFromServiceShouldBeUpdated() {
        Config current = ConfigFactory.load();
        ConfigService configService = new ConfigService(
                current,
                ConfigurationListener.IDENTITY
        );

        int expectedValue = 1;
        current = current.withValue(ConfigService.ROOT_PATH_NAME + ".test", ConfigValueFactory.fromAnyRef(expectedValue));
        configService.updateConfig(current);

        int actualValue = configService.config().getInt(ConfigService.ROOT_PATH_NAME + ".test");
        assertThat(actualValue)
                .isEqualTo(expectedValue);
    }

    @Test
    void givenConfigIsFreshlyLoadedWithVersion1_whenConfigIsUpdated_thenVersionShouldBeEqualTo2() {
        ConfigService configService = new ConfigService(
                ConfigFactory.load(),
                ConfigurationListener.IDENTITY
        );
        assertThat(configService.configVersion())
                .isEqualTo(1);

        configService.updateConfig(config -> config.withValue(ConfigService.ROOT_PATH_NAME + ".test", ConfigValueFactory.fromAnyRef(1)));

        assertThat(configService.configVersion())
                .isEqualTo(2);
    }

    @Test
    void givenValidConfigIsProvided_whenConfigIsUpdated_thenConfigListenerShouldBeUpdated() {
        var configListenerWasCalled = new AtomicBoolean(false);
        var configService = new ConfigService(
                ConfigFactory.load(),
                newConfig -> configListenerWasCalled.set(true)
        );

        configService.updateConfig(config -> config.withValue(ConfigService.ROOT_PATH_NAME + ".test", ConfigValueFactory.fromAnyRef(1)));

        assertThat(configListenerWasCalled.get())
                .isTrue();
    }
}
