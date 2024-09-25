package io.github.shoothzj.http.facade.client;

import io.github.shoothzj.http.facade.core.HttpRequest;

public interface RequestFilter {

    /**
     * Apply the filter to modify the HttpRequest before sending.
     *
     * @param request The HttpRequest to be modified.
     * @return The modified HttpRequest.
     */
    HttpRequest filter(HttpRequest request);
}
