package io.github.openfacade.http.tests;

import io.github.openfacade.http.HttpClient;
import io.github.openfacade.http.HttpClientConfig;
import io.github.openfacade.http.HttpClientFactory;
import io.github.openfacade.http.HttpMethod;
import io.github.openfacade.http.HttpRequest;
import io.github.openfacade.http.HttpResponse;
import io.github.openfacade.http.HttpServer;
import io.github.openfacade.http.HttpServerConfig;
import io.github.openfacade.http.HttpServerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpServerHeaderTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        server.addRoute("/query", HttpMethod.GET, request -> {
            String name = request.headers().get("name").get(0);
            HttpResponse response = new HttpResponse(200, name.getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String url = String.format("http://localhost:%d/query", server.listenPort());

        HttpResponse aliceResp = client.sendSync(new HttpRequest(url, HttpMethod.GET, Map.of("name", List.of("alice"))));
        Assertions.assertEquals(200, aliceResp.statusCode());
        Assertions.assertArrayEquals("alice".getBytes(), aliceResp.body());

        HttpResponse bobResp = client.sendSync(new HttpRequest(url, HttpMethod.GET, Map.of("name", List.of("bob"))));
        Assertions.assertEquals(200, bobResp.statusCode());
        Assertions.assertArrayEquals("bob".getBytes(), bobResp.body());

        client.close();
        server.stop().join();
    }
}
