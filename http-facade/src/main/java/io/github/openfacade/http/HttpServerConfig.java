/*
 * Copyright 2024 OpenFacade Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.openfacade.http;

import lombok.Setter;

@Setter
public class HttpServerConfig {
    private HttpServerEngine engine;

    private String host;

    private int port;

    private TlsConfig tlsConfig;

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
