package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.TlsConfig;
import lombok.Setter;

import java.time.Duration;

@Setter
public class HttpClientConfig {
    private final HttpClientEngine engine;

    private final Duration timeout;

    private final Duration connectTimeout;

    private final TlsConfig tlsConfig;

    private HttpClientConfig(Builder builder) {
        this.engine = builder.engine;
        this.timeout = builder.timeout;
        this.connectTimeout = builder.connectTimeout;
        this.tlsConfig = builder.tlsConfig;
    }

    public HttpClientEngine engine() {
        return engine;
    }

    public Duration timeout() {
        return timeout;
    }

    public Duration connectTimeout() {
        return connectTimeout;
    }

    public TlsConfig tlsConfig() {
        return tlsConfig;
    }

    public static class Builder {
        private HttpClientEngine engine;

        private Duration timeout;

        private Duration connectTimeout;

        private TlsConfig tlsConfig;

        public Builder engine(HttpClientEngine engine) {
            this.engine = engine;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder tlsConfig(TlsConfig tlsConfig) {
            this.tlsConfig = tlsConfig;
            return this;
        }

        public HttpClientConfig build() {
            return new HttpClientConfig(this);
        }
    }
}
