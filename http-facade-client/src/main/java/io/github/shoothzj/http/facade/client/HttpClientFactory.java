package io.github.shoothzj.http.facade.client;

public class HttpClientFactory {
    public static HttpClient createHttpClient(HttpClientConfig httpClientConfig) {
        HttpClient client;
        switch (httpClientConfig.engine()) {
            case ASYNC_HTTP_CLIENT:
                client = new AsyncHttpClient(httpClientConfig);
                break;
            case JDK:
                client = new JdkHttpClient(httpClientConfig);
                break;
            case OKHTTP:
                client = new OkhttpClient(httpClientConfig);
                break;
            default:
                throw new IllegalStateException("Unsupported HttpClient engine: " + httpClientConfig.engine());
        }

        return client;
    }
}
