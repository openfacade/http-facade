package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.HttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class BasicAuthRequestFilter implements RequestFilter {

    private final String username;

    private final String password;

    public BasicAuthRequestFilter(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public HttpRequest filter(HttpRequest request) {
        String authValue = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        request.headers().put("Authorization", List.of(authValue));
        return request;
    }
}
