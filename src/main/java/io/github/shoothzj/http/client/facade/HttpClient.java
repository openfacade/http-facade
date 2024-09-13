package io.github.shoothzj.http.client.facade;

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

    default CompletableFuture<HttpResponse> post(String url, byte[] body, Map<String, List<String>> headers) {
        return send(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.POST)
                        .body(body)
                        .headers(headers)
                        .build()
        );
    }

    default CompletableFuture<HttpResponse> put(String url, byte[] body, Map<String, List<String>> headers) {
        return send(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.PUT)
                        .body(body)
                        .headers(headers)
                        .build()
        );
    }

    default CompletableFuture<HttpResponse> delete(String url, Map<String, List<String>> headers) {
        return send(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.DELETE)
                        .headers(headers)
                        .build()
        );
    }

    default CompletableFuture<HttpResponse> get(String url, Map<String, List<String>> headers) {
        return send(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.GET)
                        .headers(headers)
                        .build()
        );
    }

    default @NotNull HttpResponse postSync(String url, byte[] body, Map<String, List<String>> headers) {
        return sendSync(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.POST)
                        .body(body)
                        .headers(headers)
                        .build()
        );
    }

    default @NotNull HttpResponse putSync(String url, byte[] body, Map<String, List<String>> headers) {
        return sendSync(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.PUT)
                        .body(body)
                        .headers(headers)
                        .build()
        );
    }

    default @NotNull HttpResponse deleteSync(String url, Map<String, List<String>> headers) {
        return sendSync(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.DELETE)
                        .headers(headers)
                        .build()
        );
    }

    default @NotNull HttpResponse getSync(String url, Map<String, List<String>> headers) {
        return sendSync(
                new HttpRequest.Builder()
                        .url(url)
                        .method(HttpMethod.GET)
                        .headers(headers)
                        .build()
        );
    }

    /**
     * Closeable method to clean up resources.
     */
    @Override
    default void close() throws IOException {
        // Default implementation for clients that may not need to clean up
    }
}
