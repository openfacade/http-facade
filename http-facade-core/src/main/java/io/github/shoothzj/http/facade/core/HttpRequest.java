package io.github.shoothzj.http.facade.core;

import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
public class HttpRequest {

    private final String url;
    private final HttpMethod method;
    private final Map<String, List<String>> headers;
    @Nullable
    private byte[] body;

    public HttpRequest(String url, HttpMethod method) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
    }

    public HttpRequest(String url, HttpMethod method, Map<String, List<String>> headers) {
        this.url = url;
        this.method = method;
        this.headers = headers;
    }

    public HttpRequest(String url, HttpMethod method, @Nullable byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = new HashMap<>();
        this.body = body;
    }

    public HttpRequest(String url, HttpMethod method, Map<String, List<String>> headers, @Nullable byte[] body) {
        this.url = url;
        this.method = method;
        this.headers = headers;
        this.body = body;
    }

    public String url() {
        return url;
    }

    public HttpMethod method() {
        return method;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    @Nullable
    public byte[] body() {
        return body;
    }
}
