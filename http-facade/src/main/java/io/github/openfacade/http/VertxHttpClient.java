package io.github.openfacade.http;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.net.JksOptions;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class VertxHttpClient extends BaseHttpClient {
    private final Vertx vertx;

    private final io.vertx.core.http.HttpClient vertxClient;

    public VertxHttpClient(HttpClientConfig config) {
        super(config);
        this.vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions();
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
        this.vertxClient = vertx.createHttpClient(options);
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) {
        CompletableFuture<HttpResponse> futureResponse = new CompletableFuture<>();
        io.vertx.core.http.HttpMethod vertxMethod = io.vertx.core.http.HttpMethod.valueOf(request.method().name());
        URI uri = URI.create(request.url());
        RequestOptions options = new RequestOptions()
                .setMethod(vertxMethod)
                .setHost(uri.getHost())
                .setPort(uri.getPort())
                .setURI(uri.getPath() + (uri.getQuery() != null ? "?" + uri.getQuery() : ""));

        vertxClient.request(options).compose(httpRequest -> {
            request.headers().forEach((key, values) -> values.forEach(value -> httpRequest.putHeader(key, value)));

            byte[] body = request.body();
            if (body != null) {
                return httpRequest.send(new String(body, StandardCharsets.UTF_8));
            } else {
                return httpRequest.send();
            }
        }).onSuccess(response -> {
            response.bodyHandler(buffer -> {
                HttpResponse httpResponse = new HttpResponse(
                        response.statusCode(),
                        buffer.getBytes(),
                        response.headers().entries().stream()
                                .collect(Collectors.groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                                ))
                );
                futureResponse.complete(httpResponse);
            });
        }).onFailure(futureResponse::completeExceptionally);

        return futureResponse;
    }

    @Override
    public void close() throws IOException {
        vertx.close();
    }
}
