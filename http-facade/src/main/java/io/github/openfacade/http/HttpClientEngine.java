package io.github.openfacade.http;

public enum HttpClientEngine {
    AsyncHttpClient,
    // jdk 11+ HttpClient
    JDK,
    // jdk 8+ HttpURLConnection
    JDK8,
    Jetty,
    OkHttp,
    Vertx,
}
