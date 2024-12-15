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

import io.github.openfacade.http.HttpMethod;
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

import java.util.concurrent.CompletableFuture;

@Slf4j
public class ReactorHttpClientHeadTest extends ReactorBaseTest {
    @ParameterizedTest
    @MethodSource("reactorClientServerConfigProvider")
    void testReactorClientServerCombinations(ReactorHttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        ReactorHttpClient client = ReactorHttpClientFactory.createReactorHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);

        HttpMethod method = HttpMethod.HEAD;
        server.addRoute("/hello", method, request -> {
            HttpResponse response = new HttpResponse(200, new byte[0]);
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String url = String.format("http://localhost:%d/hello", server.listenPort());
        log.info("sending {} request to url: {}", method, url);
        client.send(HttpMethod.HEAD, Mono.just(url), Mono.empty(), null).doOnNext(response -> {
            Assertions.assertEquals(200, response.statusCode());
            Assertions.assertEquals("", new String(response.body()));
        }).block();
        server.stop().join();
    }
}
