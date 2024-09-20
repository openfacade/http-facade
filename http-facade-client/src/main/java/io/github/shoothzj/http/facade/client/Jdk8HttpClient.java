package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.HttpRequest;
import io.github.shoothzj.http.facade.core.HttpResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class Jdk8HttpClient extends BaseHttpClient {
    private final Duration connectTimeout;

    private final Duration timeout;

    public Jdk8HttpClient(HttpClientConfig config) {
        super(config);
        this.connectTimeout = config.connectTimeout();
        this.timeout = config.timeout();
    }

    @Override
    public CompletableFuture<HttpResponse> send(HttpRequest request) {
        return CompletableFuture.supplyAsync(() -> sendSync(request));
    }

    @Override
    public HttpResponse sendSync(HttpRequest request) {
        HttpURLConnection connection = null;
        try {
            // Set up the connection
            URL url = new URL(request.url());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.method().name());

            if (connectTimeout != null) {
                connection.setConnectTimeout((int) connectTimeout.toMillis());
            }
            if (timeout != null) {
                connection.setReadTimeout((int) timeout.toMillis());
            }

            HttpURLConnection finalConnection = connection;
            request.headers().forEach((key, values) -> {
                for (String value : values) {
                    finalConnection.setRequestProperty(key, value);
                }
            });

            // Handle body for POST, PUT, etc.
            if (request.method().name().equals("POST") || request.method().name().equals("PUT")) {
                connection.setDoOutput(true);
                if (request.body() != null) {
                    connection.getOutputStream().write(request.body());
                }
            }

            int statusCode = connection.getResponseCode();
            byte[] responseBody = connection.getInputStream().readAllBytes();

            return new HttpResponse(statusCode, responseBody, connection.getHeaderFields());
        } catch (IOException e) {
            throw new HttpClientException("HttpURLConnection request failed", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
