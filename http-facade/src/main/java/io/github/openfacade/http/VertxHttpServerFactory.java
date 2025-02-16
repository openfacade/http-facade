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
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;

public class VertxHttpServerFactory {
    protected static VertxHttpServer createHttpServer(HttpServerConfig config) {
        Vertx vertx = Vertx.vertx();
        HttpServerOptions options = new HttpServerOptions();
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
        Router router = Router.router(vertx);
        HttpServer vertxServer = vertx.createHttpServer(options);
        return new VertxHttpServer(config, vertx, router, vertxServer);
    }
}
