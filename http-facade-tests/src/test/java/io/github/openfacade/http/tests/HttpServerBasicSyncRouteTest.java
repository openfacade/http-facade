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


@Slf4j
public class HttpServerBasicSyncRouteTest extends BaseTest {
    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());
        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        server.addSyncRoute("/route1", HttpMethod.GET, request -> new HttpResponse(200, "route1".getBytes()));
        server.addSyncRoute("/route2", HttpMethod.GET, request -> new HttpResponse(200, "route2".getBytes()));

        server.start().join();

        String prefix = String.format("http://localhost:%d", server.listenPort());

        HttpResponse route1Resp = client.sendSync(new HttpRequest(prefix + "/route1", HttpMethod.GET));
        Assertions.assertEquals(200, route1Resp.statusCode());
        Assertions.assertArrayEquals("route1".getBytes(), route1Resp.body());

        HttpResponse route2Resp = client.sendSync(new HttpRequest(prefix + "/route2", HttpMethod.GET));
        Assertions.assertEquals(200, route2Resp.statusCode());
        Assertions.assertArrayEquals("route2".getBytes(), route2Resp.body());

        client.close();
        server.stop().join();
    }
}
