package io.github.openfacade.http;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    public void addHeader(@NotNull String key, @NotNull String value) {
        headers.putIfAbsent(key, new ArrayList<>());
        headers.get(key).add(value);
    }

    public void addHeader(@NotNull String key, @NotNull List<String> value) {
        if (headers.get(key) == null) {
            headers.put(key, value);
        } else {
            headers.get(key).addAll(value);
        }
    }

    @Nullable
    public byte[] body() {
        return body;
    }

    public Map<String, String> pathVariables() {
        return pathVariables;
    }

    public Map<String, List<String>> queryParams() {
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }
        return queryParams;
    }

    public void addQueryParam(@NotNull String key, @NotNull String value) {
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }
        queryParams.putIfAbsent(key, new ArrayList<>());
        queryParams.get(key).add(value);
    }

    public void addQueryParam(@NotNull String key, @NotNull List<String> value) {
        if (queryParams == null) {
            queryParams = new HashMap<>();
        }
        if (queryParams.get(key) == null) {
            queryParams.put(key, value);
        } else {
            queryParams.get(key).addAll(value);
        }
    }
}
