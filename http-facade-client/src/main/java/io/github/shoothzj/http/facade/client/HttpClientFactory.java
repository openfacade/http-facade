package io.github.shoothzj.http.facade.client;

import java.lang.reflect.InvocationTargetException;

public class HttpClientFactory {
    public static HttpClient createHttpClient(HttpClientConfig httpClientConfig) {
        try {
            HttpClient client;
            switch (httpClientConfig.engine()) {
                case OKHTTP:
                    client = (HttpClient) Class.forName("io.github.shoothzj.http.facade.client.OkhttpClient")
                            .getDeclaredConstructor(HttpClientConfig.class).newInstance(httpClientConfig);
                    break;
                case ASYNC_HTTP_CLIENT:
                    client = (HttpClient) Class.forName("io.github.shoothzj.http.facade.client.AsyncHttpClient")
                            .getDeclaredConstructor(HttpClientConfig.class).newInstance(httpClientConfig);
                    break;
                default:
                    client = (HttpClient) Class.forName("io.github.shoothzj.http.facade.client.JdkHttpClient")
                            .getDeclaredConstructor(HttpClientConfig.class).newInstance(httpClientConfig);
                    break;
            }

            return client;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new IllegalStateException("Failed to create HttpClient for engine: " + httpClientConfig.engine(), e);
        }
    }
}
