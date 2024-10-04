package io.github.openfacade.http;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class JettyHttpServer extends ServletHttpServer {
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
}
