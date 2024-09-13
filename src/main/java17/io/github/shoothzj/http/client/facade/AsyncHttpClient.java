package io.github.shoothzj.http.client.facade;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AsyncHttpClient extends BaseHttpClient {

    private final org.asynchttpclient.AsyncHttpClient client;

    public AsyncHttpClient(HttpClientConfig config) {
        super(config);
        DefaultAsyncHttpClientConfig.Builder builder = Dsl.config();
        builder.setConnectTimeout(config.connectTimeout())
                .setReadTimeout(config.timeout())
                .setRequestTimeout(config.timeout());
        this.client = Dsl.asyncHttpClient(builder);
    }

    @Override
    public CompletableFuture<HttpResponse> send(HttpRequest request) throws HttpClientException {
        Request asyncRequest = buildAsyncRequest(request);
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();

        client.executeRequest(asyncRequest, new AsyncCompletionHandler<>() {
            @Override
            public @Nullable HttpResponse onCompleted(@Nullable Response response) {
                if (response == null) {
                    future.completeExceptionally(new HttpClientException("Async request failed"));
                    return null;
                }

                Map<String, List<String>> headers = response.getHeaders().entries()
                        .stream()
                        .collect(Collectors.groupingBy(Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

                HttpResponse httpResponse = new HttpResponse(response.getStatusCode(), response.getResponseBodyAsBytes(), headers);
                future.complete(httpResponse);
                return httpResponse;
            }

            @Override
            public void onThrowable(Throwable t) {
                future.completeExceptionally(new HttpClientException("Async request failed", t));
            }
        });

        return future;
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    private Request buildAsyncRequest(HttpRequest request) {
        BoundRequestBuilder builder = client.prepareGet(request.url())
                .setMethod(request.method().name());

        request.headers().forEach(builder::addHeader);
        if (request.getBody() != null) {
            builder.setBody(request.getBody());
        }

        return builder.build();
    }
}
