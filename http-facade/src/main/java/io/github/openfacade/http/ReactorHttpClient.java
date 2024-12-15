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

import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.ssl.SslContext;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientSecurityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactorHttpClient {
    private HttpClient client;

    public ReactorHttpClient(ReactorHttpClientConfig config) {
        this.client = HttpClient.create();
        client = client.responseTimeout(config.timeout());
        int connectionTimeoutMs = (int)config.connectTimeout().toMillis();
        client = client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMs);

        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();
            client = client.secure(spec -> {
                SslContext context = ReactorSslContextFactory.buildFromJks(
                    tlsConfig.keyStorePath(),
                    tlsConfig.keyStorePassword(),
                    tlsConfig.trustStorePath(),
                    tlsConfig.trustStorePassword(),
                    tlsConfig.verifyDisabled(),
                    tlsConfig.versions(),
                    tlsConfig.cipherSuites());
                if (tlsConfig.hostnameVerifyDisabled()) {
                    spec.sslContext(context)
                        .handlerConfigurator(HttpClientSecurityUtils.HOSTNAME_VERIFICATION_CONFIGURER);
                } else {
                    spec.sslContext(context);
                }
            });
        }
    }

    /**
     * Send a request asynchronously.
     *
     * @param method The HTTP Method.
     * @param url The HTTP url.
     * @param body The HTTP body.
     * @param headers The HTTP header.
     * @return A Mono that resolves to an HttpResponse.
     */
    public Mono<HttpResponse> send(HttpMethod method, Mono<String> url, Publisher<byte[]> body, @Nullable Map<String, List<String>> headers) {
        HttpClient httpClient = client;
        if (headers != null) {
            httpClient = client.headers(reactorHeaders -> {
                headers.forEach(reactorHeaders::add);
            });
        }

        if (HttpMethod.POST.equals(method)) {
            return handleResponse(httpClient.post().uri(url).send((req, out) -> out.sendByteArray(body)));
        }
        if (HttpMethod.PUT.equals(method)) {
            return handleResponse(httpClient.put().uri(url).send((req, out) -> out.sendByteArray(body)));
        }
        if (HttpMethod.DELETE.equals(method)) {
            return handleResponse(httpClient.delete().uri(url));
        }
        if (HttpMethod.GET.equals(method)) {
            return handleResponse(httpClient.get().uri(url));
        }
        if (HttpMethod.HEAD.equals(method)) {
            return handleResponse(httpClient.head().uri(url));
        }
        if (HttpMethod.PATCH.equals(method)) {
            return handleResponse(httpClient.patch().uri(url).send((req, out) -> out.sendByteArray(body)));
        }
        if (HttpMethod.OPTIONS.equals(method)) {
            return handleResponse(httpClient.options().uri(url));
        }
        throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }

    public Mono<HttpResponse> post(Mono<String> url, Publisher<byte[]> body) {
        return send(HttpMethod.POST,url, body, null);
    }

    public Mono<HttpResponse> post(Mono<String> url, Publisher<byte[]> body, Map<String, List<String>> headers) {
        return send(HttpMethod.POST,url, body, headers);
    }

    public Mono<HttpResponse> put(Mono<String> url, Publisher<byte[]> body) {
        return send(HttpMethod.PUT, url, body, null);
    }

    public Mono<HttpResponse> put(Mono<String> url, Publisher<byte[]> body, Map<String, List<String>> headers) {
        return send(HttpMethod.PUT, url, body, headers);
    }

    public Mono<HttpResponse> delete(Mono<String> url) {
        return send(HttpMethod.DELETE, url, Mono.empty(), null);
    }

    public Mono<HttpResponse> delete(Mono<String> url, Map<String, List<String>> headers) {
        return send(HttpMethod.DELETE, url, Mono.empty(), headers);
    }

    public Mono<HttpResponse> get(Mono<String> url) {
        return send(HttpMethod.GET, url, Mono.empty(), null);
    }

    public Mono<HttpResponse> get(Mono<String> url, Map<String, List<String>> headers) {
        return send(HttpMethod.GET, url, Mono.empty(), headers);
    }

    private Mono<HttpResponse> handleResponse(HttpClient.ResponseReceiver<?> responseReceiver) {
        return responseReceiver.responseSingle((response, content) -> {
            int code = response.status().code();
            HttpHeaders headers = response.responseHeaders();
            Mono<byte[]> byteBody = content.asByteArray();
            if (!headers.isEmpty()) {
                Map<String, List<String>> responseHeaders = new HashMap<>();
                headers.iteratorAsString()
                       .forEachRemaining(entry -> {
                           List<String> list = new ArrayList<>();
                           list.add(entry.getValue());
                           responseHeaders.put(entry.getKey(), list);
                       });
                return byteBody.switchIfEmpty(Mono.just(new byte[0]))
                               .map(responseBody -> new HttpResponse(code, responseBody, responseHeaders));
            }
            return byteBody.switchIfEmpty(Mono.just(new byte[0]))
                           .map(responseBody -> new HttpResponse(code, responseBody));
        });
    }
}
