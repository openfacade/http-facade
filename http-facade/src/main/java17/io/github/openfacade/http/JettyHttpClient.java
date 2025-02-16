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

import org.eclipse.jetty.client.BytesRequestContent;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.http.HttpFields;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class JettyHttpClient extends BaseHttpClient {
    private final HttpClient jettyHttpClient;
    private final Duration timeout;

    public JettyHttpClient(HttpClientConfig config, HttpClient jettyHttpClient) {
        super(config);
        this.jettyHttpClient = jettyHttpClient;
        this.timeout = config.timeout();
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) {
        HttpMethod method = request.method();
        byte[] body = request.body();
        Map<String, List<String>> headers = request.headers();
        Request jettyRequest = jettyHttpClient.newRequest(request.url()).method(method.name()).headers(
            jettyHeaders -> headers.forEach((field, values) -> jettyHeaders.add(field, values)));

        if (body != null) {
            jettyRequest.body(new BytesRequestContent(body));
        }

        if (timeout != null) {
            jettyRequest.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        CompletableFuture<HttpResponse> futureResponse = new CompletableFuture<>();
        try {
            ContentResponse jettyResponse = jettyRequest.send();
            int status = jettyResponse.getStatus();
            byte[] content = jettyResponse.getContent() == null ? new byte[0] : jettyResponse.getContent();
            HttpResponse httpResponse = new HttpResponse(status, content, convertToHeaders(jettyResponse.getHeaders()));
            futureResponse.complete(httpResponse);
        } catch (TimeoutException e) {
            futureResponse.completeExceptionally(
                new HttpClientException("Request timed out after " + timeout.getSeconds() + " seconds", e));
        } catch (InterruptedException e) {
            futureResponse.completeExceptionally(new HttpClientException("Request was interrupted", e));
        } catch (ExecutionException e) {
            futureResponse.completeExceptionally(new HttpClientException("execute request failed", e));
        }
        return futureResponse;
    }

    private Map<String, List<String>> convertToHeaders(HttpFields httpFields) {
        Map<String, List<String>> responseHeaders = new HashMap<>();
        if (httpFields != null) {
            httpFields.stream().iterator().forEachRemaining(
                httpField -> responseHeaders.put(httpField.getName(), httpField.getValueList()));
        }
        return responseHeaders;
    }

    @Override
    public void close() throws IOException {
        try {
            jettyHttpClient.close();
        } catch (Exception e) {
            throw new IOException("close jetty client failed", e);
        }
    }
}
