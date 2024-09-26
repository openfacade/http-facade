package io.github.openfacade.http;

import java.util.concurrent.CompletableFuture;

public class AsyncHttpClient extends BaseHttpClient {
    public AsyncHttpClient(HttpClientConfig config) {
        super(config);
        throw new UnsupportedOperationException("jdk11 is required for AsyncHttpClient");
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) throws HttpClientException {
        throw new UnsupportedOperationException("jdk11 is required for AsyncHttpClient");
    }
}
