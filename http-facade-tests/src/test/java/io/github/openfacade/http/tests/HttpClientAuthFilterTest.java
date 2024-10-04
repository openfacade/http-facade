package io.github.openfacade.http.tests;

import io.github.openfacade.http.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpClientAuthFilterTest extends BaseTest {
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
            if (authorization == null) {
                // tomcat will convert the header name to lower case
                authorization = headers.get("authorization");
            }
            if (authorization == null || !"Basic dXNlcm5hbWU6cGFzc3dvcmQ=".equals(authorization.get(0))) {
                return CompletableFuture.completedFuture(new HttpResponse(401, new byte[0]));
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
