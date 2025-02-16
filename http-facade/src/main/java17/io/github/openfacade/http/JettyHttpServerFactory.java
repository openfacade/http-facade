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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class JettyHttpServerFactory {
    protected static JettyHttpServer createHttpServer(HttpServerConfig config) {
        Server server = new Server();

        // Configure SSL if needed
        ServerConnector serverConnector;
        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();
            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(tlsConfig.keyStorePath());
            sslContextFactory.setKeyStorePassword(String.valueOf(tlsConfig.keyStorePassword()));
            if (tlsConfig.trustStorePath() != null) {
                sslContextFactory.setTrustStorePath(tlsConfig.trustStorePath());
                sslContextFactory.setTrustStorePassword(String.valueOf(tlsConfig.trustStorePassword()));
            }
            serverConnector = new ServerConnector(server, sslContextFactory);
        } else {
            serverConnector = new ServerConnector(server);
        }

        serverConnector.setPort(config.port() == 0 ? SocketUtil.findAvailablePort() : config.port());
        server.addConnector(serverConnector);
        return new JettyHttpServer(config, server, serverConnector);
    }
}
