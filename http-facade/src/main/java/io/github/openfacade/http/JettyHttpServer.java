package io.github.openfacade.http;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class JettyHttpServer extends BaseHttpServer {
    private final Server server;

    private final ServerConnector serverConnector;

    public JettyHttpServer(HttpServerConfig config) {
        super(config);
        server = new Server();

        // Configure SSL if needed
        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(tlsConfig.keyStorePath());
            sslContextFactory.setKeyStorePassword(String.valueOf(tlsConfig.keyStorePassword()));
            if (tlsConfig.trustStorePath() != null) {
                sslContextFactory.setTrustStorePath(tlsConfig.trustStorePath());
                sslContextFactory.setTrustStorePassword(String.valueOf(tlsConfig.trustStorePassword()));
            }
            serverConnector = new ServerConnector(server, sslContextFactory);
        } else {
            serverConnector = new ServerConnector(server);
        }

        serverConnector.setPort(config.port() == 0 ? SocketUtil.findAvailablePort() : config.port());
        server.addConnector(serverConnector);

        // Setup servlet context
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        // Add servlet handler
        context.addServlet(new ServletHolder(new RequestHandlerServlet()), "/*");
    }

    @Override
    protected void addRoute(Route route) {
    }

    @Override
    public CompletableFuture<Void> start() {
        return CompletableFuture.runAsync(() -> {
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
    public int listenPort() {
        return serverConnector.getLocalPort();
    }

    private class RequestHandlerServlet extends HttpServlet {
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) {
            try {
                String url = req.getRequestURI();

                HttpMethod method = null;
                for (HttpMethod httpMethod : JettyHttpServer.this.routes.keySet()) {
                    if (httpMethod.name().equals(req.getMethod())) {
                        method = httpMethod;
                        break;
                    }
                }
                if (method == null) {
                    resp.setStatus(405);
                    return;
                }
                Map<String, Route> routeMap = JettyHttpServer.this.routes.get(method);

                Route matchedRoute = null;
                for (Route route : routeMap.values()) {
                    if (route.pattern.matcher(url).matches()) {
                        matchedRoute = route;
                        break;
                    }
                }

                if (matchedRoute == null) {
                    resp.setStatus(404);
                    return;
                }

                byte[] body = null;
                if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.PATCH.equals(method)) {
                    body = req.getInputStream().readAllBytes();
                }
                HttpRequest httpRequest = new HttpRequest(url, method, body);

                Map<String, String> pathVariables = PathUtil.extractPathVariableNames(matchedRoute.pattern, matchedRoute.pathVariableNames, url);
                httpRequest.setPathVariables(pathVariables);

                req.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                    String headerValue = req.getHeader(headerName);
                    httpRequest.addHeader(headerName, headerValue);
                });

                matchedRoute.handler.handle(httpRequest).thenAccept(httpResponse -> {
                    try {
                        resp.setStatus(httpResponse.statusCode());
                        resp.getWriter().write(new String(httpResponse.body(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        log.error("Error writing response", e);
                    }
                });
            } catch (Exception e) {
                log.error("Error handling request", e);
            }
        }
    }
}
