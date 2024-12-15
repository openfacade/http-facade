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

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class VertxHttpServer extends BaseHttpServer {
    private final Vertx vertx;

    private final Router router;

    private final io.vertx.core.http.HttpServer vertxServer;

    public VertxHttpServer(HttpServerConfig config) {
        super(config);
        this.vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();
            options.setSsl(true)
                    .setKeyCertOptions(new JksOptions()
                            .setPath(tlsConfig.keyStorePath())
                            .setPassword(String.valueOf(tlsConfig.keyStorePassword())));
            if (tlsConfig.trustStorePath() != null) {
                options.setTrustOptions(new JksOptions()
                        .setPath(tlsConfig.trustStorePath())
                        .setPassword(String.valueOf(tlsConfig.trustStorePassword())));
            }
        }
        this.router = Router.router(vertx);
        this.vertxServer = vertx.createHttpServer(options);
    }

    @Override
    public CompletableFuture<Void> start() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        int listenPort = (config.port() == 0) ? SocketUtil.findAvailablePort() : config.port();

        vertxServer.requestHandler(router).listen(listenPort, result -> {
            if (result.succeeded()) {
                log.info("Vert.x HTTP server started on port {}", listenPort);
                future.complete(null);
            } else {
                future.completeExceptionally(new RuntimeException("Failed to start Vert.x server: " + result.cause().getMessage()));
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<Void> stop() {
        return CompletableFuture.runAsync(() -> {
            if (vertxServer != null) {
                vertxServer.close();
            }
            vertx.close();
        });
    }

    @Override
    protected void addRoute(Route route) {
        router.route(vertxHttpMethod(route.method), PathUtil.toVertxPath(route.path)).handler(ctx -> {
            HttpServerRequest req = ctx.request();
            convertToHttpRequest(req).thenCompose(httpRequest -> {
                        try {
                            httpRequest.setPathVariables(route.pathVariables(httpRequest.url()));
                            Map<String, List<String>> params = req.params().entries().stream()
                                    .collect(Collectors.groupingBy(
                                            Map.Entry::getKey,
                                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                                    ));
                            httpRequest.setQueryParams(params);
                            return route.handler.handle(httpRequest).thenAccept(response -> {
                                HttpServerResponse vertxResponse = req.response();
                                vertxResponse.setStatusCode(response.statusCode());
                                vertxResponse.end(vertxBody(response.body()));
                            });
                        } catch (Exception e) {
                            log.error("Error while handling request", e);
                            HttpServerResponse vertxResponse = req.response();
                            vertxResponse.setStatusCode(500);
                            vertxResponse.end();
                            return CompletableFuture.completedFuture(null);
                        }
                    }
            );
        });
    }

    @Override
    public int listenPort() {
        return vertxServer.actualPort();
    }

    private String vertxBody(@Nullable byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Private method to convert Vert.x HttpServerRequest to custom HttpRequest asynchronously.
     *
     * @param vertxRequest Vert.x HttpServerRequest object.
     * @return CompletableFuture<HttpRequest> that resolves to the HttpRequest.
     */
    private CompletableFuture<HttpRequest> convertToHttpRequest(HttpServerRequest vertxRequest) {
        CompletableFuture<HttpRequest> futureRequest = new CompletableFuture<>();

        String url = vertxRequest.uri();

        Map<String, List<String>> headers = vertxRequest.headers().entries().stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));

        vertxRequest.bodyHandler(buffer -> {
            byte[] body = buffer.getBytes();
            HttpRequest httpRequest = new HttpRequest(url, HttpMethod.valueOf(vertxRequest.method().name()), headers, body);
            // Complete the future when the body is fully read
            futureRequest.complete(httpRequest);
        }).exceptionHandler(futureRequest::completeExceptionally);

        return futureRequest;
    }

    private static io.vertx.core.http.HttpMethod vertxHttpMethod(HttpMethod method) {
        switch (method) {
            case GET:
                return io.vertx.core.http.HttpMethod.GET;
            case POST:
                return io.vertx.core.http.HttpMethod.POST;
            case PUT:
                return io.vertx.core.http.HttpMethod.PUT;
            case DELETE:
                return io.vertx.core.http.HttpMethod.DELETE;
            case PATCH:
                return io.vertx.core.http.HttpMethod.PATCH;
            case HEAD:
                return io.vertx.core.http.HttpMethod.HEAD;
            case OPTIONS:
                return io.vertx.core.http.HttpMethod.OPTIONS;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
}
