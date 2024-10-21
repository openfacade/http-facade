package io.github.openfacade.http;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class TomcatHttpServer extends ServletHttpServer {
    public TomcatHttpServer(HttpServerConfig config) {
        super(config);
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }

    @Override
    public CompletableFuture<Void> start() {
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }

    @Override
    public CompletableFuture<Void> stop() {
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }

    @Override
    public int listenPort() {
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }
}
