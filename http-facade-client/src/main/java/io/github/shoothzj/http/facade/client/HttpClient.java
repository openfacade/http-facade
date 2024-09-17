package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.HttpMethod;
import io.github.shoothzj.http.facade.core.HttpRequest;
import io.github.shoothzj.http.facade.core.HttpResponse;
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
        return send(new HttpRequest(url, HttpMethod.POST, headers, body));
    }

    default CompletableFuture<HttpResponse> put(String url, byte[] body, Map<String, List<String>> headers) {
        return send(new HttpRequest(url, HttpMethod.PUT, headers, body));
    }

    default CompletableFuture<HttpResponse> delete(String url, Map<String, List<String>> headers) {
        return send(new HttpRequest(url, HttpMethod.DELETE, headers));
    }

    default CompletableFuture<HttpResponse> get(String url, Map<String, List<String>> headers) {
        return send(new HttpRequest(url, HttpMethod.GET, headers));
    }

    default @NotNull HttpResponse postSync(String url, byte[] body, Map<String, List<String>> headers) {
        return sendSync(new HttpRequest(url, HttpMethod.POST, headers, body));
    }

    default @NotNull HttpResponse putSync(String url, byte[] body, Map<String, List<String>> headers) {
        return sendSync(new HttpRequest(url, HttpMethod.PUT, headers, body));
    }

    default @NotNull HttpResponse deleteSync(String url, Map<String, List<String>> headers) {
        return sendSync(new HttpRequest(url, HttpMethod.DELETE, headers));
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
