package io.github.shoothzj.http.facade.server;

import io.github.shoothzj.http.facade.core.HttpMethod;
import io.github.shoothzj.http.facade.core.HttpRequest;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
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
        vertxServer.requestHandler(req -> {
            if (req.method().name().equalsIgnoreCase(method.name()) && req.path().equals(path)) {
                convertToHttpRequest(req).thenCompose(httpRequest ->
                        handler.handle(httpRequest).thenAccept(response -> {
                            HttpServerResponse vertxResponse = req.response();
                            vertxResponse.setStatusCode(response.statusCode());
                            vertxResponse.end(vertxBody(response.body()));
                        })
                );
            }
        });
    }

    private String vertxBody(@Nullable byte[] bytes) {
        if (bytes == null) {
            return null;
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
}
