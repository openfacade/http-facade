package io.github.openfacade.http;

public class ReactorHttpClientFactory {
    public static ReactorHttpClient createReactorHttpClient(ReactorHttpClientConfig clientConfig) {
        return new ReactorHttpClient(clientConfig);
    }
}
