package io.github.shoothzj.http.facade.server;

public class HttpServerFactory {
    public static HttpServer createHttpServer(HttpServerConfig httpServerConfig) {
        HttpServer server;
        switch (httpServerConfig.engine()) {
            case Jetty:
                server = new JettyHttpServer(httpServerConfig);
                break;
            case Vertx:
                server = new VertxHttpServer(httpServerConfig);
                break;
            default:
                throw new IllegalStateException("Unsupported HttpServer engine: " + httpServerConfig.engine());
        }
        return server;
    }
}
