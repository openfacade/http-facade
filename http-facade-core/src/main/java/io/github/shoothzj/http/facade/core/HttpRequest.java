package io.github.shoothzj.http.facade.core;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    private byte[] body;

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
    }

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method,
                       @NotNull Map<String, List<String>> headers) {
        this.url = url;
        this.method = method;
        this.headers = headers;
    }

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method, @Nullable byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
        this.body = body;
    }

    public HttpRequest(@NotNull String url, @NotNull HttpMethod method, @NotNull Map<String, List<String>> headers,
                       @Nullable byte[] body) {
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

    @Nullable
    public byte[] body() {
        return body;
    }
}
