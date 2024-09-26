package io.github.openfacade.http;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;

    @NotNull
    private final byte[] body;

    @NotNull
    private final Map<String, List<String>> headers;

    public HttpResponse(int statusCode) {
        this.statusCode = statusCode;
        this.body = new byte[0];
        this.headers = new HashMap<>();
    }

    public HttpResponse(int statusCode, @NotNull byte[] body) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = new HashMap<>();
    }

    public HttpResponse(int statusCode, @NotNull byte[] body, @NotNull Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int statusCode() {
        return statusCode;
    }

    @NotNull
    public byte[] body() {
        return body;
    }

    @NotNull
    public String bodyAsString() {
        return new String(body, StandardCharsets.UTF_8);
    }

    @NotNull
    public Map<String, List<String>> headers() {
        return headers;
    }
}
