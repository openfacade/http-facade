/*
 * Copyright 2024 OpenFacade Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
