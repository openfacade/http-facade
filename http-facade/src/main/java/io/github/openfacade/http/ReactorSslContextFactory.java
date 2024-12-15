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

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import java.util.Arrays;

public class ReactorSslContextFactory {
    public static SslContext buildFromJks(String keyStorePath,
                                          char[] keyStorePassword,
                                          String trustStorePath,
                                          char[] trustStorePassword,
                                          boolean disableSslVerify,
                                          String[] tlsProtocols,
                                          String[] tlsCiphers) {

        try {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

            if (keyStorePath != null && keyStorePassword != null) {
                sslContextBuilder.keyManager(TlsUtil.initKeyManagerFactory(keyStorePath, keyStorePassword));
            }

            if (disableSslVerify) {
                sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
            } else if (trustStorePath != null && trustStorePassword != null) {
                sslContextBuilder.trustManager(TlsUtil.initTrustManagerFactory(trustStorePath, trustStorePassword));
            }

            if (tlsProtocols != null) {
                sslContextBuilder.protocols(tlsProtocols);
            }

            if (tlsCiphers != null) {
                sslContextBuilder.ciphers(Arrays.asList(tlsCiphers));
            }

            return sslContextBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up tls configuration", e);
        }
    }
}
