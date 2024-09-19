package io.github.shoothzj.http.facade.client;

public enum HttpClientEngine {
    ASYNC_HTTP_CLIENT,
    // jdk 11+ HttpClient
    JDK,
    // jdk 8+ HttpURLConnection
    JDK8,
    OKHTTP,
}
