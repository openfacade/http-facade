package io.github.shoothzj.http.facade.tests;

import io.github.shoothzj.http.facade.client.HttpClientConfig;
import io.github.shoothzj.http.facade.client.HttpClientEngine;
import io.github.shoothzj.http.facade.core.TlsConfig;
import io.github.shoothzj.http.facade.server.HttpServerConfig;
import io.github.shoothzj.http.facade.server.HttpServerEngine;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

@Slf4j
public class HttpTlsTest extends BaseTest{
    @Override
    protected List<HttpClientConfig> clientConfigList() {
        TlsConfig tlsConfig = new TlsConfig.Builder().build();
        return List.of(
                new HttpClientConfig.Builder().engine(HttpClientEngine.AsyncHttpClient).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JDK).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JDK8).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.OkHttp).tlsConfig(tlsConfig).build()
        );
    }

    @Override
    protected List<HttpServerConfig> serverConfigList() {
        TlsConfig tlsConfig = new TlsConfig.Builder().build();
        return List.of(
                new HttpServerConfig.Builder().engine(HttpServerEngine.Vertx).tlsConfig(tlsConfig).build()
        );
    }

    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
    }
}
