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

package io.github.openfacade.http;

import java.time.Duration;

public class HttpClientFactory {
    public static HttpClient createHttpClient(HttpClientConfig httpClientConfig) {
        HttpClient client;

        HttpClientEngine engine = httpClientConfig.engine() != null
                ? httpClientConfig.engine()
                : detectDefaultEngine();

        if (httpClientConfig.connectTimeout() == null) {
            httpClientConfig.setConnectTimeout(Duration.ofSeconds(10));
        }

        if (httpClientConfig.timeout() == null) {
            httpClientConfig.setTimeout(Duration.ofSeconds(30));
        }

        switch (engine) {
            case Async:
                client = AsyncHttpClientFactory.createHttpClient(httpClientConfig);
                break;
            case Java:
                client = JavaHttpClientFactory.createHttpClient(httpClientConfig);
                break;
            case Java8:
                client = Java8HttpClientFactory.createHttpClient(httpClientConfig);
                break;
            case OkHttp:
                client = OkHttpClientFactory.createHttpClient(httpClientConfig);
                break;
            case Vertx:
                client = VertxHttpClientFactory.createHttpClient(httpClientConfig);
                break;
            default:
                throw new IllegalStateException("Unsupported HttpClient engine: " + httpClientConfig.engine());
        }

        return client;
    }

    /**
     * Detects the default engine based on the availability of java.net.HttpClient.
     * If java.net.HttpClient is available (Java 11+), return JDK. Otherwise, return HTTP_URL_CONNECTION.
     *
     * @return HttpClientEngine - the default engine
     */
    private static HttpClientEngine detectDefaultEngine() {
        try {
            // Attempt to load java.net.HttpClient
            Class.forName("java.net.http.HttpClient");
            return HttpClientEngine.Java;
        } catch (ClassNotFoundException e) {
            // If java.net.HttpClient is not available (Java 8), fall back to HttpURLConnection
            return HttpClientEngine.Java8;
        }
    }
}
