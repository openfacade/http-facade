package io.github.shoothzj.http.facade.core;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public class HttpRequest {
    @NotNull
    private final String url;

    @NotNull
    private final HttpMethod method;

    @NotNull
    private final Map<String, List<String>> headers;

    @NotNull
    private byte[] body;

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
        this.body = new byte[0];
    }

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method, @NotNull Map<String, List<String>> headers) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = new byte[0];
    }

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method, @NotNull byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
        this.body = body;
    }

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method, @NotNull Map<String, List<String>> headers,
                       @NotNull byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    @NotNull
    public String url() {
        return url;
    }

    @NotNull
    public HttpMethod method() {
        return method;
    }

    @NotNull
    public Map<String, List<String>> headers() {
        return headers;
    }

    @NotNull
    public byte[] body() {
        return body;
    }
}
