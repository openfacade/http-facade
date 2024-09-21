package io.github.shoothzj.http.facade.server;

import io.github.shoothzj.http.facade.core.HttpMethod;
import io.github.shoothzj.http.facade.core.HttpRequest;
import io.github.shoothzj.http.facade.core.TlsConfig;
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

    private io.vertx.core.http.HttpServer vertxServer;

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
        this.vertxServer = vertx.createHttpServer();
    }

    @Override
    public CompletableFuture<Void> start() {
        return CompletableFuture.runAsync(() -> {
            final int listenPort;
            if (config.port() == 0) {
                listenPort = SocketUtil.findAvailablePort();
            } else {
                listenPort = config.port();
            }
            vertxServer.requestHandler(router).listen(listenPort, result -> {
                if (result.succeeded()) {
                    log.info("Vert.x HTTP server started on port {}", listenPort);
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
        router.route(vertxHttpMethod(method), path).handler(ctx -> {
            HttpServerRequest req = ctx.request();
            convertToHttpRequest(req).thenCompose(httpRequest ->
                    handler.handle(httpRequest).thenAccept(response -> {
                        HttpServerResponse vertxResponse = req.response();
                        vertxResponse.setStatusCode(response.statusCode());
                        vertxResponse.end(vertxBody(response.body()));
                    })
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
