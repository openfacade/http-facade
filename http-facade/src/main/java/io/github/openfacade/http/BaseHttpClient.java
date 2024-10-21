package io.github.openfacade.http;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

abstract class BaseHttpClient implements HttpClient {
    protected final HttpClientConfig config;

    private Duration timeout = Duration.ofSeconds(30);

    public BaseHttpClient(HttpClientConfig config) {
        this.config = config;
        if (this.config.timeout() != null) {
            this.timeout = this.config.timeout();
        }
    }

    @Override
    public CompletableFuture<HttpResponse> send(HttpRequest request) {
        for (RequestFilter requestFilter : config.requestFilters()) {
            request = requestFilter.filter(request);
        }
        return innerSend(request);
    }

    protected abstract CompletableFuture<HttpResponse> innerSend(HttpRequest request);

    /**
     * Send a request synchronously with the configured timeout.
     *
     * @param request The HTTP request to send.
     * @return The HTTP response.
     */
    @Override
    public HttpResponse sendSync(HttpRequest request) {
        try {
            // Wait for the async operation to complete within the specified timeout
            CompletableFuture<HttpResponse> future = this.send(request);
            return future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);  // Use the configured timeout
        } catch (TimeoutException e) {
            throw new HttpClientException("Request timed out after " + timeout.getSeconds() + " seconds", e);
        } catch (Exception e) {
            throw new HttpClientException("Failed to execute synchronous request", e);
        }
    }
}
