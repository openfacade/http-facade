package io.github.openfacade.http.tests;

import io.github.openfacade.http.HttpClient;
import io.github.openfacade.http.HttpClientConfig;
import io.github.openfacade.http.HttpClientEngine;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpClientPatchTest extends BaseTest {
    @Override
    protected List<HttpClientConfig> clientConfigList() {
        return List.of(
                new HttpClientConfig.Builder().engine(HttpClientEngine.AsyncHttpClient).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JAVA).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.OkHttp).build()
        );
    }

    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        HttpMethod method = HttpMethod.PATCH;
        server.addRoute("/hello", method, request -> {
            HttpResponse response = new HttpResponse(200, String.format("%s method called!", method).getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String url = String.format("http://localhost:%d/hello", server.listenPort());

        HttpRequest request = new HttpRequest(url, method);
        if (clientConfig.engine().equals(HttpClientEngine.OkHttp)) {
            request.setBody("".getBytes(StandardCharsets.UTF_8));
        }
        log.info("sending {} request to url: {}", method, url);
        HttpResponse response = client.sendSync(request);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(String.format("%s method called!", method), new String(response.body()));

        client.close();
        server.stop().join();
    }
}
