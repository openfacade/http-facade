package io.github.openfacade.http;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class JettyHttpServer extends BaseHttpServer {
    private Server server;

    public JettyHttpServer(HttpServerConfig config) {
        super(config);
    }

    @Override
    public CompletableFuture<Void> start() {
        return CompletableFuture.runAsync(() -> {
            server = new Server(config.port());
            try {
                server.start();
                log.info("Jetty HTTP server started on port {}", config.port());
            } catch (Exception e) {
                throw new RuntimeException("Failed to start Jetty server: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> stop() {
        return CompletableFuture.runAsync(() -> {
            try {
                if (server != null) {
                    server.stop();
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to stop Jetty server: " + e.getMessage(), e);
            }
        });
    }

    @Override
    protected void addRoute(Route route) {
    }

    @Override
    public int listenPort() {
        return -1;
    }
}