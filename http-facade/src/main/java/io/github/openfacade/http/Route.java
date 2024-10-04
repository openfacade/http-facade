package io.github.openfacade.http;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.regex.Pattern;

@AllArgsConstructor
public class Route {
    final HttpMethod method;

    final String path;

    final Pattern pattern;

    final String[] pathVariableNames;

    final RequestHandler handler;

    public Map<String, String> pathVariables(String url) {
        return PathUtil.extractPathVariableNames(this.pattern, this.pathVariableNames, url);
    }
}
