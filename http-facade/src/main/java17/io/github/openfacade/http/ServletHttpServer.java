package io.github.openfacade.http;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public abstract class ServletHttpServer extends BaseHttpServer{
    public ServletHttpServer(HttpServerConfig config) {
        super(config);
    }

    @Override
    protected void addRoute(Route route) {
    }

    protected class RequestHandlerServlet extends HttpServlet {
        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) {
            try {
                HttpMethod method = null;
                for (HttpMethod httpMethod : ServletHttpServer.this.routes.keySet()) {
                    if (httpMethod.name().equals(req.getMethod())) {
                        method = httpMethod;
                        break;
                    }
                }
                if (method == null) {
                    resp.setStatus(405);
                    return;
                }

                Map<String, Route> routeMap = ServletHttpServer.this.routes.get(method);
                String url = req.getRequestURI();
                Route matchedRoute = null;
                for (Route route : routeMap.values()) {
                    if (route.pattern.matcher(url).matches()) {
                        matchedRoute = route;
                        break;
                    }
                }

                if (matchedRoute == null) {
                    resp.setStatus(404);
                    return;
                }

                byte[] body = null;
                if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.PATCH.equals(method)) {
                    body = req.getInputStream().readAllBytes();
                }

                HttpRequest httpRequest = new HttpRequest(url, method, body);
                httpRequest.setPathVariables(matchedRoute.pathVariables(url));

                req.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                    String headerValue = req.getHeader(headerName);
                    httpRequest.addHeader(headerName, headerValue);
                });

                Map<String, String[]> queryParams = req.getParameterMap();
                queryParams.forEach((key, values) -> {
                    for (String value : values) {
                        httpRequest.addQueryParam(key, value);
                    }
                });

                matchedRoute.handler.handle(httpRequest).thenAccept(httpResponse -> {
                    try {
                        resp.setStatus(httpResponse.statusCode());
                        resp.getWriter().write(new String(httpResponse.body(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        log.error("Error writing response", e);
                    }
                });
            } catch (Exception e) {
                log.error("Error handling request", e);
            }
        }
    }
}
