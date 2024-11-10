package io.github.openfacade.http;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class UrlBuilder {
    private HttpSchema httpSchema = HttpSchema.HTTP;
    private String host = null;
    private int port = 8080;
    private String path = null;
    private List<Param> queryParams = null;

    public UrlBuilder addParameter(@NotNull String key, @NotNull String value) {
        if (this.queryParams == null) {
            queryParams = new LinkedList<>();
        }
        this.queryParams.add(new Param(key, value));
        return this;
    }

    public UrlBuilder setHttpSchema(@NotNull HttpSchema httpSchema) {
        this.httpSchema = httpSchema;
        return this;
    }

    public UrlBuilder setHost(@NotNull String host) {
        this.host = host;
        return this;
    }

    public UrlBuilder setPort(int port) {
        this.port = port;
        return this;
    }

    public UrlBuilder setPath(@NotNull String path) {
        this.path = path;
        return this;
    }

    public void setQueryParams(@NotNull List<Param> queryParams) {
        this.queryParams = queryParams;
    }

    public String build() {
        StringBuilder url = new StringBuilder();
        if (this.host == null) {
            throw new IllegalArgumentException("host is required.");
        }

        url.append(String.format("%s://%s:%s", this.httpSchema.getName(), this.host, this.port));
        if (this.path != null) {
            url.append(path);
        }

        if (this.queryParams == null || this.queryParams.isEmpty()) {
            return url.toString();
        }
        url.append("?");
        url.append(this.queryParams.get(0).toQueryString());
        for (int i = 1; i < this.queryParams.size(); i++) {
            url.append("&");
            url.append(this.queryParams.get(i).toQueryString());
        }
        return url.toString();
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("failed to encode url", e);
        }
    }

    public UrlBuilder duplicate() {
        UrlBuilder urlBuilder = new UrlBuilder();
        urlBuilder.setHttpSchema(this.httpSchema);
        urlBuilder.setPort(this.port);

        if (this.host != null) {
            urlBuilder.setHost(this.host);
        }
        if (this.path != null) {
            urlBuilder.setPath(this.path);
        }
        if (this.queryParams != null) {
            urlBuilder.setQueryParams(new LinkedList<>(this.queryParams));
        }
        return urlBuilder;
    }

    public static class Param {
        private final String key;
        private final String value;

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        protected String toQueryString() {
            return encode(key) + "=" + encode(value);
        }
    }
}
