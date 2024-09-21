package io.github.shoothzj.http.facade.server;

import io.github.shoothzj.http.facade.core.TlsConfig;
import lombok.Setter;

@Setter
public class HttpServerConfig {
    private final HttpServerEngine engine;

    private final String host;

    private final int port;

    private final TlsConfig tlsConfig;

    private HttpServerConfig(Builder builder) {
        this.engine = builder.engine;
        this.host = builder.host;
        this.port = builder.port;
        this.tlsConfig = builder.tlsConfig;
    }

    public HttpServerEngine engine() {
        return engine;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public TlsConfig tlsConfig() {
        return tlsConfig;
    }

    public static class Builder {
        private HttpServerEngine engine;

        private String host;

        private int port;

        private TlsConfig tlsConfig;

        public Builder engine(HttpServerEngine engine) {
            this.engine = engine;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder tlsConfig(TlsConfig tlsConfig) {
            this.tlsConfig = tlsConfig;
            return this;
        }

        public HttpServerConfig build() {
            return new HttpServerConfig(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HttpServerConfig{\n");
        sb.append("engine=").append(engine).append("\n");
        sb.append("host=").append(host).append("\n");
        sb.append("port=").append(port).append("\n");
        if (tlsConfig != null) {
            sb.append("tlsConfig=").append(tlsConfig).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
