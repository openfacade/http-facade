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
import io.github.shoothzj.http.facade.server.HttpServerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpBasicPutTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        HttpMethod method = HttpMethod.PUT;
        server.addRoute("/hello", method, request -> {
            HttpResponse response;
            if (method.equals(HttpMethod.HEAD)) {
                response = new HttpResponse(200, null);
            } else {
                response = new HttpResponse(200, String.format("%s method called!", method).getBytes());
            }
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
