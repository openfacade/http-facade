package io.github.openfacade.http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BasicAuthRequestFilter implements RequestFilter {

    private final List<String> list = new ArrayList<>();

    public BasicAuthRequestFilter(String username, String password) {
        String authValue = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
        this.list.add(authValue);
    }

    @Override
    public HttpRequest filter(HttpRequest request) {
        request.headers().put("Authorization", list);
        return request;
    }
}
