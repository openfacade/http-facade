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

import java.time.Duration;

@Setter
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
