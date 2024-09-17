package io.github.shoothzj.http.facade.server;

import java.lang.reflect.InvocationTargetException;

public class HttpServerFactory {
    public static HttpServer createHttpServer(HttpServerConfig httpServerConfig) {
        try {
            HttpServer server;
            switch (httpServerConfig.engine()) {
                case Jetty:
                    server = (HttpServer) Class.forName("io.github.shoothzj.http.facade.server.VertxHttpServer")
                            .getDeclaredConstructor(HttpServerConfig.class).newInstance(httpServerConfig);
                    break;
                default:
                    server = (HttpServer) Class.forName("io.github.shoothzj.http.facade.server.JettyHttpServer")
                            .getDeclaredConstructor(HttpServerConfig.class).newInstance(httpServerConfig);
                    break;
            }
            return server;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException("Failed to create HttpServer for engine: " + httpServerConfig.engine(), e);
        }
    }
}
