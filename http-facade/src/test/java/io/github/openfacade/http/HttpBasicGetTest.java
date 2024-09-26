package io.github.openfacade.http;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpBasicGetTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        HttpMethod method = HttpMethod.GET;
        server.addRoute("/hello", method, request -> {
            HttpResponse response = new HttpResponse(200, String.format("%s method called!", method).getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String url = String.format("http://localhost:%d/hello", server.listenPort());

        HttpRequest request = new HttpRequest(url, method);
        log.info("sending {} request to url: {}", method, url);
        HttpResponse response = client.sendSync(request);

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(String.format("%s method called!", method), new String(response.body()));

        client.close();
        server.stop().join();
    }
}
