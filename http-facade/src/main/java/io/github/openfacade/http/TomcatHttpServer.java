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

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class TomcatHttpServer extends ServletHttpServer {
    public TomcatHttpServer(HttpServerConfig config) {
        super(config);
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }

    @Override
    public CompletableFuture<Void> start() {
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }

    @Override
    public CompletableFuture<Void> stop() {
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }

    @Override
    public int listenPort() {
        throw new UnsupportedOperationException("jdk17 is required for TomcatHttpServer");
    }
}
