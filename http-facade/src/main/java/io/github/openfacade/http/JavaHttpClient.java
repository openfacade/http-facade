package io.github.openfacade.http;

import java.util.concurrent.CompletableFuture;

public class JavaHttpClient extends BaseHttpClient{
    public JavaHttpClient(HttpClientConfig config) {
        super(config);
        throw new UnsupportedOperationException("jdk17 is required for JavaHttpClient");
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) {
        throw new UnsupportedOperationException("jdk17 is required for JavaHttpClient");
    }
}
