package io.github.openfacade.http;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ServletHttpServer extends BaseHttpServer{
    public ServletHttpServer(HttpServerConfig config) {
        super(config);
    }

    @Override
    protected void addRoute(Route route) {
        throw new UnsupportedOperationException("jdk17 is required for ServletHttpServer");
    }
}
