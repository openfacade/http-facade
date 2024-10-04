package io.github.openfacade.http;

import java.util.concurrent.CompletableFuture;

public interface HttpServer {
    /**
     * Start the server asynchronously.
     *
     * @return a CompletableFuture that completes when the server is started.
     */
    CompletableFuture<Void> start();

    /**
     * Stop the server asynchronously.
     *
     * @return a CompletableFuture that completes when the server is stopped.
     */
    CompletableFuture<Void> stop();

    /**
     * Add a route to handle HTTP requests for a given path and method.
     *
     * @param path the route path.
     * @param method the HTTP method (GET, POST, PUT, DELETE, etc.).
     * @param handler the request handler for this route.
     */
    void addRoute(String path, HttpMethod method, RequestHandler handler);

    /**
     * Add a route to handle HTTP requests for a given path and method.
     *
     * @param path the route path.
     * @param method the HTTP method (GET, POST, PUT, DELETE, etc.).
     * @param handler the request handler for this route.
     */
    void addSyncRoute(String path, HttpMethod method, SyncRequestHandler handler);

    /**
     * Get the port the server is listening on.
     *
     * @return the port number.
     */
    int listenPort();
}
