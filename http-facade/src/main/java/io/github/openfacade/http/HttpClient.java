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

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface HttpClient extends Closeable {

    /**
     * Send a request asynchronously.
     *
     * @param request The HTTP request to be sent.
     * @return A CompletableFuture that resolves to an HttpResponse.
     */
    CompletableFuture<HttpResponse> send(HttpRequest request);

    /**
     * Send a request synchronously with the configured timeout, defaulting to 30 seconds.
     *
     * @param request The HTTP request to send.
     * @return The HTTP response.
     */
    HttpResponse sendSync(HttpRequest request);

    default CompletableFuture<HttpResponse> post(String url, byte[] body) {
        return send(new HttpRequest(url, HttpMethod.POST, body));
    }

    default CompletableFuture<HttpResponse> post(String url, byte[] body, Map<String, List<String>> headers) {
        return send(new HttpRequest(url, HttpMethod.POST, headers, body));
    }

    default CompletableFuture<HttpResponse> put(String url, byte[] body) {
        return send(new HttpRequest(url, HttpMethod.PUT, body));
    }

    default CompletableFuture<HttpResponse> put(String url, byte[] body, Map<String, List<String>> headers) {
        return send(new HttpRequest(url, HttpMethod.PUT, headers, body));
    }

    default CompletableFuture<HttpResponse> delete(String url) {
        return send(new HttpRequest(url, HttpMethod.DELETE));
    }

    default CompletableFuture<HttpResponse> delete(String url, Map<String, List<String>> headers) {
        return send(new HttpRequest(url, HttpMethod.DELETE, headers));
    }

    default CompletableFuture<HttpResponse> get(String url) {
        return send(new HttpRequest(url, HttpMethod.GET));
    }

    default CompletableFuture<HttpResponse> get(String url, Map<String, List<String>> headers) {
        return send(new HttpRequest(url, HttpMethod.GET, headers));
    }

    default @NotNull HttpResponse postSync(String url, byte[] body) {
        return sendSync(new HttpRequest(url, HttpMethod.POST, body));
    }

    default @NotNull HttpResponse postSync(String url, byte[] body, Map<String, List<String>> headers) {
        return sendSync(new HttpRequest(url, HttpMethod.POST, headers, body));
    }

    default @NotNull HttpResponse putSync(String url, byte[] body) {
        return sendSync(new HttpRequest(url, HttpMethod.PUT, body));
    }

    default @NotNull HttpResponse putSync(String url, byte[] body, Map<String, List<String>> headers) {
        return sendSync(new HttpRequest(url, HttpMethod.PUT, headers, body));
    }

    default @NotNull HttpResponse deleteSync(String url) {
        return sendSync(new HttpRequest(url, HttpMethod.DELETE));
    }

    default @NotNull HttpResponse deleteSync(String url, Map<String, List<String>> headers) {
        return sendSync(new HttpRequest(url, HttpMethod.DELETE, headers));
    }

    default @NotNull HttpResponse getSync(String url) {
        return sendSync(new HttpRequest(url, HttpMethod.GET));
    }

    default @NotNull HttpResponse getSync(String url, Map<String, List<String>> headers) {
        return sendSync(new HttpRequest(url, HttpMethod.GET, headers));
    }

    /**
     * Closeable method to clean up resources.
     */
    @Override
    default void close() throws IOException {
        // Default implementation for clients that may not need to clean up
    }
}
