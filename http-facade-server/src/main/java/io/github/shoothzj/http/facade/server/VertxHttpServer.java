package io.github.shoothzj.http.facade.server;

import io.github.shoothzj.http.facade.core.HttpMethod;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class VertxHttpServer extends BaseHttpServer {
    private final Vertx vertx;

    private io.vertx.core.http.HttpServer vertxServer;

    public VertxHttpServer(HttpServerConfig config) {
        super(config);
        this.vertx = Vertx.vertx();
    }

    @Override
    public CompletableFuture<Void> start() {
        return CompletableFuture.runAsync(() -> {
            vertxServer = vertx.createHttpServer();
            vertxServer.listen(config.port(), result -> {
                if (result.succeeded()) {
                    log.info("Vert.x HTTP server started on port {}", config.port());
                } else {
                    throw new RuntimeException("Failed to start Vert.x server: " + result.cause().getMessage());
                }
            });
        });
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
    public void addRoute(String path, HttpMethod method, RequestHandler handler) {
    }
}
