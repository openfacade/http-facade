package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.HttpRequest;
import io.github.shoothzj.http.facade.core.HttpResponse;

import java.util.concurrent.CompletableFuture;

public class AsyncHttpClient extends BaseHttpClient {
    public AsyncHttpClient(HttpClientConfig config) {
        super(config);
        throw new UnsupportedOperationException("jdk11 is required for AsyncHttpClient");
    }

    @Override
    public CompletableFuture<HttpResponse> send(HttpRequest request) throws HttpClientException {
        throw new UnsupportedOperationException("jdk11 is required for AsyncHttpClient");
    }
}
