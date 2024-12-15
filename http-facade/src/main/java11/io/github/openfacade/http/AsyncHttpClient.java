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

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncHttpClient extends BaseHttpClient {

    private final org.asynchttpclient.AsyncHttpClient client;

    public AsyncHttpClient(HttpClientConfig config) {
        super(config);
        DefaultAsyncHttpClientConfig.Builder builder = Dsl.config();
        if (config.connectTimeout() != null) {
            builder = builder.setConnectTimeout(config.connectTimeout());
        }
        if (config.timeout() != null) {
            builder = builder.setReadTimeout(config.timeout())
                    .setRequestTimeout(config.timeout());
        }
        this.client = Dsl.asyncHttpClient(builder);
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) throws HttpClientException {
        Request asyncRequest = buildAsyncRequest(request);
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();

        client.executeRequest(asyncRequest, new AsyncCompletionHandler<>() {
            @Override
            public @Nullable HttpResponse onCompleted(@Nullable Response response) {
                if (response == null) {
                    future.completeExceptionally(new HttpClientException("Async request failed"));
                    return null;
                }

                Map<String, List<String>> headers = response.getHeaders().entries()
                        .stream()
                        .collect(Collectors.groupingBy(Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

                HttpResponse httpResponse = new HttpResponse(response.getStatusCode(), response.getResponseBodyAsBytes(), headers);
                future.complete(httpResponse);
                return httpResponse;
            }

            @Override
            public void onThrowable(Throwable t) {
                future.completeExceptionally(new HttpClientException("Async request failed", t));
            }
        });

        return future;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    private Request buildAsyncRequest(HttpRequest request) {
        BoundRequestBuilder builder = client.prepareGet(request.url())
                .setMethod(request.method().name());

        request.headers().forEach(builder::addHeader);
        if (request.body() != null) {
            builder.setBody(request.body());
        }

        return builder.build();
    }
}
