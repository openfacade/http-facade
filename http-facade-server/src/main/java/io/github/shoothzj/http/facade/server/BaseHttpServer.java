package io.github.shoothzj.http.facade.server;

abstract class BaseHttpServer implements HttpServer {
    protected HttpServerConfig config;

    public BaseHttpServer(HttpServerConfig config) {
        this.config = config;
    }
}
