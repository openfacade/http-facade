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
import io.github.openfacade.http.ReactorHttpClient;
import io.github.openfacade.http.ReactorHttpClientConfig;
import io.github.openfacade.http.ReactorHttpClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpServerParamsTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        server.addRoute("/query", HttpMethod.GET, request -> {
            String name = request.queryParams().get("name").get(0);
            HttpResponse response = new HttpResponse(200, name.getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String prefix = String.format("http://localhost:%d/query", server.listenPort());

        HttpResponse aliceResp = client.sendSync(new HttpRequest(prefix + "?name=alice", HttpMethod.GET));
        Assertions.assertEquals(200, aliceResp.statusCode());
        Assertions.assertArrayEquals("alice".getBytes(), aliceResp.body());

        HttpResponse bobResp = client.sendSync(new HttpRequest(prefix + "?name=bob", HttpMethod.GET));
        Assertions.assertEquals(200, bobResp.statusCode());
        Assertions.assertArrayEquals("bob".getBytes(), bobResp.body());

        client.close();
        server.stop().join();
    }

    @ParameterizedTest
    @MethodSource("reactorClientServerConfigProvider")
    void testReactorClientServerCombinations(ReactorHttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        ReactorHttpClient client = ReactorHttpClientFactory.createReactorHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        server.addRoute("/query", HttpMethod.GET, request -> {
            String name = request.queryParams().get("name").get(0);
            HttpResponse response = new HttpResponse(200, name.getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String prefix = String.format("http://localhost:%d/query", server.listenPort());
        client.get(Mono.just(prefix + "?name=alice"),null).doOnSuccess(response -> {
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("alice", new String(response.body()));
        }).block(Duration.ofSeconds(5));
        client.get(Mono.just(prefix + "?name=bob"),null).doOnSuccess(response -> {
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("bob", new String(response.body()));
        }).block(Duration.ofSeconds(5));

        server.stop().join();
    }
}
