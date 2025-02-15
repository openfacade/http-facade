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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class OkHttpClient extends BaseHttpClient {

    private final okhttp3.OkHttpClient client;

    public OkHttpClient(HttpClientConfig config, okhttp3.OkHttpClient client) {
        super(config);
        this.client = client;
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) throws HttpClientException {
        Request okHttpRequest = buildOkHttpRequest(request);
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();

        client.newCall(okHttpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(new HttpClientException("Async request failed", e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                future.complete(buildHttpResponse(response));
            }
        });

        return future;
    }

    @Override
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    private Request buildOkHttpRequest(HttpRequest request) {
        Request.Builder builder = new Request.Builder()
                .url(request.url())
                .method(request.method().name(), requestBody(request.body()));

        request.headers().forEach((name, values) -> {
            for (String value : values) {
                builder.addHeader(name, value);
            }
        });

        return builder.build();
    }

    private @NotNull HttpResponse buildHttpResponse(@NotNull Response response) throws IOException {
        byte[] body = getBody(response.body());
        return new HttpResponse(
                response.code(),
                body,
                response.headers().toMultimap()
        );
    }

    @Nullable
    private static RequestBody requestBody(@Nullable byte[] body) {
        if (body == null) {
            return null;
        }
        return RequestBody.create(body);
    }

    @NotNull
    private static byte[] getBody(@Nullable ResponseBody responseBody) throws IOException {
        if (responseBody != null) {
            return responseBody.bytes();
        }
        return new byte[0];
    }
}
