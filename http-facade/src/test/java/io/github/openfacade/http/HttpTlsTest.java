package io.github.openfacade.http;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class HttpTlsTest extends BaseTest{
    private static final char[] JKS_PASSWORD = "changeit".toCharArray();

    public static String keyJksPath() {
        return HttpTlsTest.class.getClassLoader().getResource("jks/testkeystore.jks").getPath();
    }

    public static String trustJksPath() {
        return HttpTlsTest.class.getClassLoader().getResource("jks/testkeystore.jks").getPath();
    }

    @Override
    protected List<HttpClientConfig> clientConfigList() {
        TlsConfig tlsConfig = new TlsConfig.Builder()
                .keyStore(keyJksPath(), JKS_PASSWORD)
                .trustStore(trustJksPath(), JKS_PASSWORD)
                .build();
        return List.of(
                new HttpClientConfig.Builder().engine(HttpClientEngine.AsyncHttpClient).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JDK).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.JDK8).tlsConfig(tlsConfig).build(),
                new HttpClientConfig.Builder().engine(HttpClientEngine.OkHttp).tlsConfig(tlsConfig).build()
        );
    }

    @Override
    protected List<HttpServerConfig> serverConfigList() {
        TlsConfig tlsConfig = new TlsConfig.Builder()
                .keyStore(keyJksPath(), JKS_PASSWORD)
                .trustStore(trustJksPath(), JKS_PASSWORD)
                .build();
        return List.of(
                new HttpServerConfig.Builder().engine(HttpServerEngine.Vertx).tlsConfig(tlsConfig).build()
        );
    }

    @ParameterizedTest
    @MethodSource("clientServerConfigProvider")
    void testHttpClientServerCombinations(HttpClientConfig clientConfig, HttpServerConfig serverConfig) throws Exception {
        log.info("client engine {}, server engine {}", clientConfig.engine(), serverConfig.engine());

        HttpClient client = HttpClientFactory.createHttpClient(clientConfig);
        HttpServer server = HttpServerFactory.createHttpServer(serverConfig);
        server.addRoute("/hello", HttpMethod.GET, request -> {
            HttpResponse response = new HttpResponse(200, "Hello Tls!".getBytes());
            return CompletableFuture.completedFuture(response);
        });

        server.start().join();

        String url = String.format("https://localhost:%d/hello", server.listenPort());

        client.close();
        server.stop().join();
    }
}
