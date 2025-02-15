/*
 * Copyright 2025 OpenFacade Authors
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

import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class OkHttpClientFactory {
    protected static OkHttpClient createHttpClient(HttpClientConfig config) {
        okhttp3.OkHttpClient.Builder okHttpClientBuilder = new okhttp3.OkHttpClient.Builder();

        if (config.connectTimeout() != null) {
            okHttpClientBuilder.connectTimeout(config.connectTimeout());
        }
        if (config.timeout() != null) {
            okHttpClientBuilder.readTimeout(config.timeout())
                               .writeTimeout(config.timeout());
        }

        if (config.okHttpConfig() != null) {
            okHttpClientBuilder.retryOnConnectionFailure(config.okHttpConfig().retryOnConnectionFailure());
            HttpClientConfig.OkHttpConfig.ConnectionPoolConfig poolConfig = config.okHttpConfig().connectionPoolConfig();
            if (poolConfig != null) {
                int maxIdleConnections = poolConfig.maxIdleConnections();
                if (maxIdleConnections < 0) {
                    throw new IllegalArgumentException("maxIdleConnections should not be negative.");
                }

                Duration duration = poolConfig.keepAliveDuration();
                if (duration == null || duration.isNegative()) {
                    throw new IllegalArgumentException("keepAliveDuration should not be null or negative.");
                }
                long keepAliveNanos = duration.toNanos();
                okHttpClientBuilder.connectionPool(
                    new ConnectionPool(maxIdleConnections, keepAliveNanos, TimeUnit.NANOSECONDS));
            }
        }

        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();

            // set tls version and cipher suits
            ConnectionSpec.Builder connectionSpecBuilder = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS);
            if (tlsConfig.versions() != null) {
                connectionSpecBuilder.tlsVersions(tlsConfig.versions());
            }
            if (tlsConfig.cipherSuites() != null) {
                connectionSpecBuilder.cipherSuites(tlsConfig.cipherSuites());
            }
            ConnectionSpec connectionSpec = connectionSpecBuilder.build();

            okHttpClientBuilder.connectionSpecs(Collections.singletonList(connectionSpec));

            // create ssl context from keystore and truststore
            OkHttpSslContextFactory.OkHttpSslContext sslContext = OkHttpSslContextFactory.createOkHttpSslContext(
                tlsConfig);
            okHttpClientBuilder.sslSocketFactory(sslContext.sslSocketFactory, sslContext.x509TrustManager);

            // override hostnameVerifier to make it always success when hostname verification has been disabled
            if (tlsConfig.hostnameVerifyDisabled()) {
                okHttpClientBuilder.hostnameVerifier((s, sslSession) -> true);
            }
        }

        okhttp3.OkHttpClient client = okHttpClientBuilder.build();
        return new OkHttpClient(config, client);
    }
}
