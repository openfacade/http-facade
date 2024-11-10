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

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.time.Duration;

public class JettyHttpClientFactory {
    protected static JettyHttpClient createHttpClient(HttpClientConfig config) {
        HttpClient jettyHttpClient = new HttpClient();
        Duration connectTimeout = config.connectTimeout();
        jettyHttpClient.setConnectTimeout(connectTimeout.toMillis());

        TlsConfig tlsConfig = config.tlsConfig();
        if (tlsConfig != null) {
            ClientConnector clientConnector = new ClientConnector();
            SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
            clientConnector.setSslContextFactory(sslContextFactory);
            sslContextFactory.setKeyStorePassword(String.valueOf(tlsConfig.keyStorePassword()));
            sslContextFactory.setKeyStorePath(String.valueOf(tlsConfig.keyStorePath()));
            sslContextFactory.setTrustStorePassword(String.valueOf(tlsConfig.trustStorePassword()));
            sslContextFactory.setTrustStorePath(String.valueOf(tlsConfig.trustStorePath()));

            if (tlsConfig.hostnameVerifyDisabled()) {
                sslContextFactory.setHostnameVerifier((s, sslSession) -> true);
            }
            jettyHttpClient.setSslContextFactory(sslContextFactory);
        }

        try {
            jettyHttpClient.start();
        } catch (Exception e) {
            throw new HttpClientException("failed to start jetty client", e);
        }
        return new JettyHttpClient(config, jettyHttpClient);
    }
}
