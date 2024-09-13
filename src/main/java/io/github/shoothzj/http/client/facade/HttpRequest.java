package io.github.shoothzj.http.client.facade;

import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String url;
    private final HttpMethod method;
    private final Map<String, List<String>> headers;
    private final byte[] body;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.body = builder.body;
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

    public byte[] getBody() {
        return body;
    }

    public static class Builder {
        private String url;
        private HttpMethod method = HttpMethod.GET; // Default to GET
        private Map<String, List<String>> headers;
        private byte[] body;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder headers(Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
