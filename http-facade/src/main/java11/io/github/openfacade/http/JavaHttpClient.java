package io.github.openfacade.http;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class JavaHttpClient extends BaseHttpClient {
    private final java.net.http.HttpClient client;

    public JavaHttpClient(HttpClientConfig config) {
        super(config);
        java.net.http.HttpClient.Builder builder = java.net.http.HttpClient.newBuilder()
                .version(java.net.http.HttpClient.Version.HTTP_1_1)
                .followRedirects(java.net.http.HttpClient.Redirect.NORMAL);
        if (config.connectTimeout() != null) {
            builder = builder.connectTimeout(config.connectTimeout());
        }
        if (config.tlsConfig() != null) {
            builder = builder.sslContext(JdkSslContextUtil.buildSSLContextFromJks(
                    config.tlsConfig().keyStorePath(),
                    config.tlsConfig().keyStorePassword(),
                    config.tlsConfig().trustStorePath(),
                    config.tlsConfig().trustStorePassword(),
                    config.tlsConfig().verifyDisabled()));
        }
        this.client = builder.build();
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) throws HttpClientException {
        java.net.http.HttpRequest jdkRequest = buildJdkHttpRequest(request);

        return client.sendAsync(jdkRequest, java.net.http.HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(response -> new HttpResponse(response.statusCode(), response.body(), response.headers().map()));
    }

    private java.net.http.HttpRequest buildJdkHttpRequest(HttpRequest request) {
        java.net.http.HttpRequest.Builder builder = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(request.url()))
                .timeout(config.timeout())
                .method(request.method().name(), request.body() != null ? java.net.http.HttpRequest.BodyPublishers.ofByteArray(request.body()) : java.net.http.HttpRequest.BodyPublishers.noBody());

        request.headers().forEach((name, values) -> {
            for (String value : values) {
                builder.header(name, value);
            }
        });
        return builder.build();
    }

}
