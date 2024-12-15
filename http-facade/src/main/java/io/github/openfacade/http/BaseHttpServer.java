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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

abstract class BaseHttpServer implements HttpServer {
    protected final HttpServerConfig config;

    protected final Map<HttpMethod, Map<String, Route>> routes = new HashMap<>();

    public BaseHttpServer(HttpServerConfig config) {
        this.config = config;
    }

    @Override
    public void addRoute(String path, HttpMethod method, RequestHandler handler) {
        Route route = toRoute(path, method, handler);
        routes.computeIfAbsent(method, k -> new HashMap<>()).put(path, route);
        this.addRoute(route);
    }

    @Override
    public void addSyncRoute(String path, HttpMethod method, SyncRequestHandler handler) {
        addRoute(path, method, request -> CompletableFuture.completedFuture(handler.handle(request)));
    }

    protected abstract void addRoute(Route route);

    private static Route toRoute(String path, HttpMethod method, RequestHandler handler) {
        String regexPath = PathUtil.pathToRegex(path);
        String[] pathVariableNames = PathUtil.extractPathVariableKeys(path);
        return new Route(method, path, Pattern.compile(regexPath), pathVariableNames, handler);
    }
}
