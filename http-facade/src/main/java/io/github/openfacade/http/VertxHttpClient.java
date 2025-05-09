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

import io.vertx.core.Vertx;
import io.vertx.core.http.RequestOptions;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class VertxHttpClient extends BaseHttpClient {
    private final Vertx vertx;

    private final io.vertx.core.http.HttpClient vertxClient;

    public VertxHttpClient(HttpClientConfig config, Vertx vertx, io.vertx.core.http.HttpClient vertxClient) {
        super(config);
        this.vertx = vertx;
        this.vertxClient = vertxClient;
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) {
        CompletableFuture<HttpResponse> futureResponse = new CompletableFuture<>();
        io.vertx.core.http.HttpMethod vertxMethod = io.vertx.core.http.HttpMethod.valueOf(request.method().name());
        URI uri = URI.create(request.url());
        RequestOptions options = new RequestOptions()
                .setMethod(vertxMethod)
                .setHost(uri.getHost())
                .setPort(uri.getPort())
                .setURI(uri.getPath() + (uri.getQuery() != null ? "?" + uri.getQuery() : ""));

        vertxClient.request(options).compose(httpRequest -> {
                    request.headers().forEach((key, values) -> values.forEach(value -> httpRequest.putHeader(key, value)));

                    byte[] body = request.body();
                    if (body != null) {
                        return httpRequest.send(new String(body, StandardCharsets.UTF_8));
                    } else {
                        return httpRequest.send();
                    }
                }).onSuccess(response -> response.bodyHandler(buffer -> {
                    HttpResponse httpResponse = new HttpResponse(
                            response.statusCode(),
                            buffer.getBytes(),
                            response.headers().entries().stream()
                                    .collect(Collectors.groupingBy(
                                            Map.Entry::getKey,
                                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                                    ))
                    );
                    futureResponse.complete(httpResponse);
                }).exceptionHandler(e -> futureResponse.completeExceptionally(new HttpClientException("Read response failed", e))))
                .onFailure(e -> futureResponse.completeExceptionally(new HttpClientException("Async request failed", e)));

        return futureResponse;
    }

    @Override
    public void close() throws IOException {
        vertx.close();
    }
}
