package io.github.openfacade.http;

import java.time.Duration;

public class ReactorHttpClientConfig {
    private Duration timeout;

    private Duration connectTimeout;

    private TlsConfig tlsConfig;

    private ReactorHttpClientConfig(Builder builder) {
        this.timeout = builder.timeout;
        this.connectTimeout = builder.connectTimeout;
        this.tlsConfig = builder.tlsConfig;
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
        private Duration timeout;

        private Duration connectTimeout;

        private TlsConfig tlsConfig;

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

        public ReactorHttpClientConfig build() {
            return new ReactorHttpClientConfig(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReactorHttpClientConfig{\n");
        if (timeout != null) {
            sb.append("timeout=").append(timeout).append("\n");
        }
        if (connectTimeout != null) {
            sb.append("connectTimeout=").append(connectTimeout).append("\n");
        }
        if (tlsConfig != null) {
            sb.append("tlsConfig=").append(tlsConfig).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
