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

import io.github.openfacade.http.HttpClientConfig;
import io.github.openfacade.http.HttpClientEngine;
import io.github.openfacade.http.HttpServerConfig;
import io.github.openfacade.http.HttpServerEngine;
import io.github.openfacade.http.ReactorHttpClientConfig;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {
    protected List<HttpClientConfig> clientConfigList() {
        return List.of(
                new HttpClientConfig.Builder().engine(HttpClientEngine.Async).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.Java).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.Java8).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.Jetty).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.OkHttp).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.Vertx).build()
        );
    }

    protected List<HttpServerConfig> serverConfigList() {
        return List.of(
                new HttpServerConfig.Builder().host("127.0.0.1").engine(HttpServerEngine.Jetty).build(),
                new HttpServerConfig.Builder().host("127.0.0.1").engine(HttpServerEngine.Tomcat).build(),
                new HttpServerConfig.Builder().host("127.0.0.1").engine(HttpServerEngine.Vertx).build()
        );
    }

    protected ReactorHttpClientConfig reactorHttpClientConfig() {
        ReactorHttpClientConfig reactorClientConfig = new ReactorHttpClientConfig.Builder().build();
        return reactorClientConfig;
    }

    protected Stream<Arguments> clientServerConfigProvider() {
        List<HttpClientConfig> httpClientConfigs = clientConfigList();
        List<HttpServerConfig> httpServerConfigs = serverConfigList();

        return httpClientConfigs.stream()
                .flatMap(clientConfig -> httpServerConfigs.stream()
                        .map(serverConfig -> Arguments.arguments(clientConfig, serverConfig))
                );
    }

    protected Stream<Arguments> reactorClientServerConfigProvider() {
        List<HttpServerConfig> httpServerConfigs = serverConfigList();
        ReactorHttpClientConfig reactorClientConfig = reactorHttpClientConfig();
        return httpServerConfigs.stream().map(serverConfig -> Arguments.arguments(reactorClientConfig, serverConfig));
    }
}
