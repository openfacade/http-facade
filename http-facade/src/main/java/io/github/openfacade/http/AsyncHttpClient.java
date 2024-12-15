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

import java.util.concurrent.CompletableFuture;

public class AsyncHttpClient extends BaseHttpClient {
    public AsyncHttpClient(HttpClientConfig config) {
        super(config);
        throw new UnsupportedOperationException("jdk11 is required for AsyncHttpClient");
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) throws HttpClientException {
        throw new UnsupportedOperationException("jdk11 is required for AsyncHttpClient");
    }
}
