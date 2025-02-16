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

import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;

public class AsyncHttpClientFactory {
    protected static AsyncHttpClient createHttpClient(HttpClientConfig config) {
        DefaultAsyncHttpClientConfig.Builder builder = Dsl.config();
        if (config.connectTimeout() != null) {
            builder = builder.setConnectTimeout(config.connectTimeout());
        }
        if (config.timeout() != null) {
            builder = builder.setReadTimeout(config.timeout())
                             .setRequestTimeout(config.timeout());
        }
        org.asynchttpclient.AsyncHttpClient asyncClient = Dsl.asyncHttpClient(builder);
        return new AsyncHttpClient(config, asyncClient);
    }
}
