# HTTP Facade

`HTTP Facade` is a flexible Java library that provides a unified API for both HTTP clients and HTTP servers. The library supports multiple underlying implementations (e.g., OkHttp, AsyncHttpClient for clients, and Netty, Jetty for servers) and provides easy configuration of HTTP requests and responses, connection timeouts, TLS settings, routing, and more.

## Repository Structure

This repository is organized into the following modules:

- **http-facade-core**: Contains core utilities, shared classes, request/response models, and configuration logic used across both HTTP clients and servers.
- **http-facade-client**: The HTTP client module, which supports multiple client implementations (OkHttp, AsyncHttpClient, and JDK HttpClient).
- **http-facade-server**: The HTTP server module, supporting multiple server implementations (Jetty, Vertx).

## Client README

For more information on the HTTP client module, see the [README](http-facade-client/README.md).

## Server README

For more information on the HTTP server module, see the [README](http-facade-server/README.md).
