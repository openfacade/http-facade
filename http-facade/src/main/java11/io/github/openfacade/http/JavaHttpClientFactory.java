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

public class JavaHttpClientFactory {
    protected static JavaHttpClient createHttpClient(HttpClientConfig config) {
        java.net.http.HttpClient.Builder builder = java.net.http.HttpClient.newBuilder()
                                                                           .version(java.net.http.HttpClient.Version.HTTP_1_1)
                                                                           .followRedirects(java.net.http.HttpClient.Redirect.NORMAL);
        if (config.connectTimeout() != null) {
            builder = builder.connectTimeout(config.connectTimeout());
        }
        if (config.tlsConfig() != null) {
            builder = builder.sslContext(JdkSslContextUtil.buildSSLContextFromJks(
                config.tlsConfig().keyStorePath(),
                config.tlsConfig().keyStorePassword(),
                config.tlsConfig().trustStorePath(),
                config.tlsConfig().trustStorePassword(),
                config.tlsConfig().verifyDisabled()));
        }
        java.net.http.HttpClient client = builder.build();
        return new JavaHttpClient(config, client);
    }
}
