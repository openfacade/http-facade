package io.github.openfacade.http;

import java.time.Duration;

public class ReactorHttpClientFactory {
    public static ReactorHttpClient createReactorHttpClient(ReactorHttpClientConfig clientConfig) {
        Duration timeout = clientConfig.timeout();
        if (timeout == null) {
            clientConfig.setTimeout(Duration.ofSeconds(30));
        }
        Duration connectTimeout = clientConfig.connectTimeout();
        if (connectTimeout == null) {
            clientConfig.setConnectTimeout(Duration.ofSeconds(30));
        }
        return new ReactorHttpClient(clientConfig);
    }
}
