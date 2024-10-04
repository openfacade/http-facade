package io.github.openfacade.http;

@FunctionalInterface
public interface SyncRequestHandler {
    /**
     * Handle an HTTP request and return an HTTP response.
     * @param request
     * @return
     */
    HttpResponse handle(HttpRequest request);
}
