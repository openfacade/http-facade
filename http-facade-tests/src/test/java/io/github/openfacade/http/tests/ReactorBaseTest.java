package io.github.openfacade.http.tests;

import io.github.openfacade.http.HttpServerConfig;
import io.github.openfacade.http.ReactorHttpClientConfig;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ReactorBaseTest extends BaseTest {
    protected ReactorHttpClientConfig reactorHttpClientConfig() {
        ReactorHttpClientConfig reactorClientConfig = new ReactorHttpClientConfig.Builder().build();
        return reactorClientConfig;
    }

    protected Stream<Arguments> reactorClientServerConfigProvider() {
        List<HttpServerConfig> httpServerConfigs = serverConfigList();
        ReactorHttpClientConfig reactorClientConfig = reactorHttpClientConfig();
        return httpServerConfigs.stream().map(serverConfig -> Arguments.arguments(reactorClientConfig, serverConfig));
    }
}
