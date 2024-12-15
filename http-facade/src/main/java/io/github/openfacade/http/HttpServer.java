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
