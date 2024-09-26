package io.github.openfacade.http;

public enum HttpClientEngine {
    AsyncHttpClient,
    // jdk 11+ HttpClient
    JAVA,
    // jdk 8+ HttpURLConnection
    JAVA8,
    Jetty,
    OkHttp,
    Vertx,
}
