package io.github.openfacade.http;

import lombok.AllArgsConstructor;

import java.util.regex.Pattern;

@AllArgsConstructor
public class Route {
    final HttpMethod method;

    final String path;

    final Pattern pattern;

    final String[] pathVariableNames;

    final RequestHandler handler;
}
