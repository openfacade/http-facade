package io.github.shoothzj.http.facade.tests;

import io.github.shoothzj.http.facade.client.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpBasicAuthFilterTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        ArrayList<RequestFilter> requestFilters = new ArrayList<>();
        requestFilters.add(new BasicAuthRequestFilter("username", "password"));
        clientConfig.setRequestFilters(requestFilters);
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        HttpMethod method = HttpMethod.GET;
        server.addRoute("/hello", method, request -> {
            Map<String, List<String>> headers = request.headers();
            List<String> authorization = headers.get("Authorization");
            if (authorization == null || !"Basic dXNlcm5hbWU6cGFzc3dvcmQ=".equals(authorization.get(0))) {
                return CompletableFuture.completedFuture(new HttpResponse(401, null));
            }
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
