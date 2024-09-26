package io.github.openfacade.http;

import java.time.Duration;

public class HttpClientFactory {
    public static HttpClient createHttpClient(HttpClientConfig httpClientConfig) {
        HttpClient client;

        HttpClientEngine engine = httpClientConfig.engine() != null
                ? httpClientConfig.engine()
                : detectDefaultEngine();

        if (httpClientConfig.connectTimeout() == null) {
            httpClientConfig.setConnectTimeout(Duration.ofSeconds(10));
        }

        if (httpClientConfig.timeout() == null) {
            httpClientConfig.setTimeout(Duration.ofSeconds(30));
        }

        switch (engine) {
            case AsyncHttpClient:
                client = new AsyncHttpClient(httpClientConfig);
                break;
            case JAVA:
                client = new JavaHttpClient(httpClientConfig);
                break;
            case JAVA8:
                client = new Java8HttpClient(httpClientConfig);
                break;
            case OkHttp:
                client = new OkHttpClient(httpClientConfig);
                break;
            default:
                throw new IllegalStateException("Unsupported HttpClient engine: " + httpClientConfig.engine());
        }

        return client;
    }

    /**
     * Detects the default engine based on the availability of java.net.HttpClient.
     * If java.net.HttpClient is available (Java 11+), return JDK. Otherwise, return HTTP_URL_CONNECTION.
     *
     * @return HttpClientEngine - the default engine
     */
    private static HttpClientEngine detectDefaultEngine() {
        try {
            // Attempt to load java.net.HttpClient
            Class.forName("java.net.http.HttpClient");
            return HttpClientEngine.JAVA;
        } catch (ClassNotFoundException e) {
            // If java.net.HttpClient is not available (Java 8), fall back to HttpURLConnection
            return HttpClientEngine.JAVA8;
        }
    }
}
