package io.github.shoothzj.http.client.facade;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;

    @Nullable
    private final byte[] body;

    private final Map<String, List<String>> headers;

    public HttpResponse(int statusCode, @Nullable byte[] body, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    public int statusCode() {
        return statusCode;
    }

    @Nullable
    public byte[] body() {
        return body;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }
}
