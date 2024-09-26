package io.github.openfacade.http;

import java.util.concurrent.CompletableFuture;

public class JdkHttpClient extends BaseHttpClient{
    public JdkHttpClient(HttpClientConfig config) {
        super(config);
        throw new UnsupportedOperationException("jdk17 is required for JdkHttpClient");
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) {
        throw new UnsupportedOperationException("jdk17 is required for JdkHttpClient");
    }
}
