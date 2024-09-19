package io.github.shoothzj.http.facade.tests;

import io.github.shoothzj.http.facade.client.HttpClient;
import io.github.shoothzj.http.facade.client.HttpClientConfig;
import io.github.shoothzj.http.facade.client.HttpClientEngine;
import io.github.shoothzj.http.facade.client.HttpClientFactory;
import io.github.shoothzj.http.facade.core.HttpMethod;
import io.github.shoothzj.http.facade.core.HttpRequest;
import io.github.shoothzj.http.facade.core.HttpResponse;
import io.github.shoothzj.http.facade.server.HttpServer;
import io.github.shoothzj.http.facade.server.HttpServerConfig;
import io.github.shoothzj.http.facade.server.HttpServerEngine;
import io.github.shoothzj.http.facade.server.HttpServerFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class HttpBasicGetTest {

    private static Stream<Arguments> clientServerConfigProvider() {
        List<HttpClientConfig> httpClientConfigs = List.of(
                new HttpClientConfig.Builder().engine(HttpClientEngine.ASYNC_HTTP_CLIENT).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JDK).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JDK8).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.OKHTTP).build()
        );

        List<HttpServerConfig> httpServerConfigs = List.of(
                new HttpServerConfig.Builder().engine(HttpServerEngine.Vertx).build()
        );

        return httpClientConfigs.stream()
                .flatMap(clientConfig -> httpServerConfigs.stream()
                        .map(serverConfig -> Arguments.arguments(clientConfig, serverConfig))
                );
    }

    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        // Initialize HttpClient and HttpServer inside the test case using the provided configurations
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        server.addRoute("/hello", HttpMethod.GET, request -> {
            HttpResponse response = new HttpResponse(200, "Hello, World!".getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String url = String.format("http://localhost:%d/hello", server.listenPort());
        HttpRequest request = new HttpRequest(url, HttpMethod.GET);
        HttpResponse response = client.sendSync(request);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals("Hello, World!", new String(response.body()));

        client.close();
        server.stop().join();
    }
}
