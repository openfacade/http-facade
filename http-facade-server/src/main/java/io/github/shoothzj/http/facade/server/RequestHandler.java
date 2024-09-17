package io.github.shoothzj.http.facade.server;

import io.github.shoothzj.http.facade.core.HttpRequest;
import io.github.shoothzj.http.facade.core.HttpResponse;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface RequestHandler {
    /**
     * Handle an HTTP request asynchronously and return a CompletableFuture of an HTTP response.
     *
     * @param request the incoming request.
     * @return a CompletableFuture of the HTTP response.
     */
    CompletableFuture<HttpResponse> handle(HttpRequest request);
}
