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

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.JksOptions;

public class VertxHttpClientFactory {
    protected static VertxHttpClient createHttpClient(HttpClientConfig config) {
        Vertx vertx = Vertx.vertx();
        HttpClientOptions options = new HttpClientOptions();
        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();
            options.setSsl(true)
                   .setKeyCertOptions(new JksOptions()
                       .setPath(tlsConfig.keyStorePath())
                       .setPassword(String.valueOf(tlsConfig.keyStorePassword())));
            if (tlsConfig.trustStorePath() != null) {
                options.setTrustOptions(new JksOptions()
                    .setPath(tlsConfig.trustStorePath())
                    .setPassword(String.valueOf(tlsConfig.trustStorePassword())));
            }
        }
        HttpClient vertxClient = vertx.createHttpClient(options);
        return new VertxHttpClient(config, vertx, vertxClient);
    }
}
