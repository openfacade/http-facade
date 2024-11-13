package io.github.openfacade.http.tests;

import io.github.openfacade.http.HttpClientConfig;
import io.github.openfacade.http.HttpClientEngine;
import io.github.openfacade.http.HttpMethod;
import io.github.openfacade.http.HttpResponse;
import io.github.openfacade.http.HttpServer;
import io.github.openfacade.http.HttpServerConfig;
import io.github.openfacade.http.HttpServerEngine;
import io.github.openfacade.http.HttpServerFactory;
import io.github.openfacade.http.ReactorHttpClient;
import io.github.openfacade.http.ReactorHttpClientConfig;
import io.github.openfacade.http.ReactorHttpClientFactory;
import io.github.openfacade.http.TlsConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ReactorHttpTlsTest extends ReactorBaseTest {
    private static final char[] JKS_PASSWORD = "changeit".toCharArray();

    public static String keyJksPath() {
        return ReactorHttpTlsTest.class.getClassLoader().getResource("jks/testkeystore.jks").getPath();
    }

    public static String trustJksPath() {
        return ReactorHttpTlsTest.class.getClassLoader().getResource("jks/testkeystore.jks").getPath();
    }

    @Override
    protected List<HttpClientConfig> clientConfigList() {
        TlsConfig tlsConfig = new TlsConfig.Builder()
                .keyStore(keyJksPath(), JKS_PASSWORD)
                .trustStore(trustJksPath(), JKS_PASSWORD)
                .build();
        return List.of(
                new HttpClientConfig.Builder().engine(HttpClientEngine.AsyncHttpClient).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JAVA).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JAVA8).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.OkHttp).tlsConfig(tlsConfig).build()
        );
    }

    @Override
    protected List<HttpServerConfig> serverConfigList() {
        TlsConfig tlsConfig = new TlsConfig.Builder()
                .keyStore(keyJksPath(), JKS_PASSWORD)
                .trustStore(trustJksPath(), JKS_PASSWORD)
                .build();
        return List.of(
                new HttpServerConfig.Builder().engine(HttpServerEngine.Vertx).tlsConfig(tlsConfig).build()
        );
    }

    @Override
    protected ReactorHttpClientConfig reactorHttpClientConfig() {
        TlsConfig tlsConfig = new TlsConfig.Builder()
            .keyStore(keyJksPath(), JKS_PASSWORD)
            .trustStore(trustJksPath(), JKS_PASSWORD)
            .build();
        return new ReactorHttpClientConfig.Builder().tlsConfig(tlsConfig).build();
    }

    @ParameterizedTest
    @MethodSource("reactorClientServerConfigProvider")
    void testReactorHttpClientServerCombinations(ReactorHttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        ReactorHttpClient client = ReactorHttpClientFactory.createReactorHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);
        server.addRoute("/hello", HttpMethod.GET, request -> {
            HttpResponse response = new HttpResponse(200, "Hello Tls!".getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String url = String.format("https://localhost:%d/hello", server.listenPort());
        client.get(Mono.just(url)).doOnNext(response -> {
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("Hello Tls!", new String(response.body()));
        }).block(Duration.ofSeconds(5));
        server.stop().join();
    }
}
