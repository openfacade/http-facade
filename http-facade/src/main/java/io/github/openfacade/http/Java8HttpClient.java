package io.github.openfacade.http;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class Java8HttpClient extends BaseHttpClient {
    private final Duration connectTimeout;

    private final Duration timeout;

    public Java8HttpClient(HttpClientConfig config) {
        super(config);
        this.connectTimeout = config.connectTimeout();
        this.timeout = config.timeout();
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) {
        return CompletableFuture.supplyAsync(() -> sendSync(request));
    }

    @Override
    public HttpResponse sendSync(HttpRequest request) {
        if (config.requestFilters() != null ) {
            for (RequestFilter requestFilter : config.requestFilters()) {
                request = requestFilter.filter(request);
            }
        }
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
                byte[] body = request.body();
                if (body != null) {
                    connection.getOutputStream().write(body);
                }
            }

            int statusCode = connection.getResponseCode();

            // jdk11+ can directly use byte[] responseBody = connection.getInputStream().readAllBytes();
            byte[] responseBody = getBytes(connection);

            return new HttpResponse(statusCode, responseBody, connection.getHeaderFields());
        } catch (IOException e) {
            throw new HttpClientException("HttpURLConnection request failed", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @NotNull
    private static byte[] getBytes(HttpURLConnection connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
