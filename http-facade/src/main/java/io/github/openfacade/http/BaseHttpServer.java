package io.github.openfacade.http;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

abstract class BaseHttpServer implements HttpServer {
    protected HttpServerConfig config;

    private final Map<HttpMethod, Map<String, Route>> routes = new HashMap<>();

    public BaseHttpServer(HttpServerConfig config) {
        this.config = config;
    }

    @Override
    public void addRoute(String path, HttpMethod method, RequestHandler handler) {
        Route route = toRoute(path, method, handler);
        routes.computeIfAbsent(method, k -> new HashMap<>()).put(path, route);
        this.addRoute(route);
    }

    protected abstract void addRoute(Route route);

    private static Route toRoute(String path, HttpMethod method, RequestHandler handler) {
        String regexPath = PathUtil.pathToRegex(path);
        String[] pathVariableNames = PathUtil.extractPathVariableKeys(path);
        return new Route(method, path, Pattern.compile(regexPath), pathVariableNames, handler);
    }
}
