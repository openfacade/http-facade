package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.HttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, List<String>> headers = new HashMap<>(request.headers());

        // Add the Authorization header
        headers.put("Authorization", List.of(authValue));

        return new HttpRequest(request.url(), request.method(), headers, request.body());
    }
}
