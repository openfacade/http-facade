package io.github.openfacade.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class OkhttpClient extends BaseHttpClient {

    private final OkHttpClient client;

    public OkhttpClient(HttpClientConfig config) {
        super(config);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        if (config.connectTimeout() != null) {
            okHttpClientBuilder.connectTimeout(config.connectTimeout());
        }
        if (config.timeout() != null) {
            okHttpClientBuilder.readTimeout(config.timeout())
                    .writeTimeout(config.timeout());
        }

        if (config.okHttpConfig() != null) {
            okHttpClientBuilder.retryOnConnectionFailure(config.okHttpConfig().retryOnConnectionFailure());
            HttpClientConfig.OkHttpConfig.ConnectionPoolConfig poolConfig = config.okHttpConfig().connectionPoolConfig();
            if (poolConfig != null) {
                int maxIdleConnections = poolConfig.maxIdleConnections();
                long keepAliveNanos = poolConfig.keepAliveDuration().toNanos();
                if (maxIdleConnections > 0 && keepAliveNanos > 0) {
                    okHttpClientBuilder.connectionPool(new ConnectionPool(maxIdleConnections, keepAliveNanos, TimeUnit.NANOSECONDS));
                }
            }
        }

        if (config.tlsConfig() != null) {
            TlsConfig tlsConfig = config.tlsConfig();

            // set tls version and cipher suits
            ConnectionSpec.Builder connectionSpecBuilder = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS);
            if (tlsConfig.versions() != null) {
                connectionSpecBuilder.tlsVersions(tlsConfig.versions());
            }
            if (tlsConfig.cipherSuites() != null) {
                connectionSpecBuilder.cipherSuites(tlsConfig.cipherSuites());
            }
            ConnectionSpec connectionSpec = connectionSpecBuilder.build();

            okHttpClientBuilder.connectionSpecs(Collections.singletonList(connectionSpec));

            // create ssl context from keystore and truststore
            OkHttpSslContextFactory.OkHttpSslContext sslContext = OkHttpSslContextFactory.createOkHttpSslContext(
                    tlsConfig);
            okHttpClientBuilder.sslSocketFactory(sslContext.sslSocketFactory, sslContext.x509TrustManager);

            // override hostnameVerifier to make it always success when hostname verification has been disabled
            if (tlsConfig.hostnameVerifyDisabled()) {
                okHttpClientBuilder.hostnameVerifier((s, sslSession) -> true);
            }
        }

        this.client = okHttpClientBuilder.build();
    }

    @Override
    protected CompletableFuture<HttpResponse> innerSend(HttpRequest request) throws HttpClientException {
        Request okHttpRequest = buildOkHttpRequest(request);
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();

        client.newCall(okHttpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                future.completeExceptionally(new HttpClientException("Async request failed", e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                future.complete(buildHttpResponse(response));
            }
        });

        return future;
    }

    @Override
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    private Request buildOkHttpRequest(HttpRequest request) {
        Request.Builder builder = new Request.Builder()
                .url(request.url())
                .method(request.method().name(), requestBody(request.body()));

        request.headers().forEach((name, values) -> {
            for (String value : values) {
                builder.addHeader(name, value);
            }
        });

        return builder.build();
    }

    private @NotNull HttpResponse buildHttpResponse(@NotNull Response response) throws IOException {
        byte[] body = getBody(response.body());
        return new HttpResponse(
                response.code(),
                body,
                response.headers().toMultimap()
        );
    }

    @Nullable
    private static RequestBody requestBody(@Nullable byte[] body) {
        if (body == null) {
            return null;
        }
        return RequestBody.create(body);
    }

    @NotNull
    private static byte[] getBody(@Nullable ResponseBody responseBody) throws IOException {
        if (responseBody != null) {
            return responseBody.bytes();
        }
        return new byte[0];
    }
}
