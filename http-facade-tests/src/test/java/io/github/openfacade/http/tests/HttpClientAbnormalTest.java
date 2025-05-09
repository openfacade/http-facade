/*
 * Copyright 2024 OpenFacade Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.openfacade.http.tests;

import io.github.openfacade.http.HttpClient;
import io.github.openfacade.http.HttpClientConfig;
import io.github.openfacade.http.HttpClientEngine;
import io.github.openfacade.http.HttpClientFactory;
import io.github.openfacade.http.HttpMethod;
import io.github.openfacade.http.HttpRequest;
import io.github.openfacade.http.SocketUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Slf4j
public class HttpClientAbnormalTest extends BaseTest {
    private static final int TIMEOUT_SEC = 2;

    @ParameterizedTest
    @MethodSource("clientConfigProvider")
    void testServerResponseTimeout(HttpClientConfig clientConfig) throws Exception {
        Handler<HttpServerRequest> httpServerRequestHandler = request -> {
            // respond nothing
        };
        HttpServer server = mockTimeoutServer(httpServerRequestHandler);

        HttpClient httpClient = initClientWithTimeoutConf(clientConfig, TIMEOUT_SEC);
        log.info("sending timeout request, engine {}", clientConfig.engine().name());
        String url = String.format("http://localhost:%d/", server.actualPort());
        HttpClient client = sendTimeoutReq(url, httpClient);

        client.close();
        closeServer(server);
    }

    @ParameterizedTest
    @MethodSource("clientConfigProvider")
    void testServerChunkTimeout(HttpClientConfig clientConfig) throws Exception {
        // Java11 client currently doesn't support timeout for chunked data.
        if (clientConfig.engine().equals(HttpClientEngine.Java)) {
            return;
        }
        Handler<HttpServerRequest> httpServerRequestHandler = request -> {
            // respond one chunk but not respond end signal, to mock client socket timeout
            request.response().setChunked(true);
            request.response().write("chunk1");
        };
        HttpServer server = mockTimeoutServer(httpServerRequestHandler);

        HttpClient httpClient = initClientWithTimeoutConf(clientConfig, TIMEOUT_SEC);
        log.info("sending chunk timeout request, engine {}", clientConfig.engine().name());
        String url = String.format("http://localhost:%d/", server.actualPort());
        HttpClient client = sendTimeoutReq(url, httpClient);
        client.close();
        closeServer(server);
    }

    private static HttpClient initClientWithTimeoutConf(HttpClientConfig clientConfig, int timeoutSec) {
        clientConfig.setTimeout(Duration.ofSeconds(timeoutSec));
        return HttpClientFactory.createHttpClient(clientConfig);
    }

    private static HttpClient sendTimeoutReq(String url, HttpClient client) {
        HttpMethod method = HttpMethod.GET;
        HttpRequest request = new HttpRequest(url, method);
        Assertions.assertThrows(ExecutionException.class, () -> client.send(request).get());
        return client;
    }

    private static HttpServer mockTimeoutServer(Handler<HttpServerRequest> httpServerRequestHandler) {
        Vertx vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
        int port = SocketUtil.findAvailablePort();
        options.setPort(port);
        options.setHost("localhost");
        HttpServer server = vertx.createHttpServer(options);
        server.requestHandler(httpServerRequestHandler).listen(event -> {
            if (event.succeeded()) {
                log.info("success to start vertx mock timeout server");
            } else {
                log.error("fail to start vertx mock timeout server");
            }
        });
        return server;
    }

    private static void closeServer(HttpServer server) {
        server.close(event -> log.info("success to close vertx mock timeout server"));
    }
}
