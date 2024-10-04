package io.github.openfacade.http;

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

    /**
     * for server side, the path variables are extracted from the url
     */
    private Map<String, String> pathVariables;

    /**
     * for server side, the query parameters are extracted from the url
     */
    private Map<String, List<String>> queryParams;

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

    public Map<String, String> pathVariables() {
        return pathVariables;
    }

    public Map<String, List<String>> queryParams() {
        return queryParams;
    }
}
