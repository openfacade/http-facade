package io.github.openfacade.http;

abstract class BaseHttpServer implements HttpServer {
    protected HttpServerConfig config;

    public BaseHttpServer(HttpServerConfig config) {
        this.config = config;
    }
}
