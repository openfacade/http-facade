# HTTP Facade

![License](https://img.shields.io/badge/license-Apache2.0-green)
[![OpenSSF Best Practices](https://www.bestpractices.dev/projects/11002/badge)](https://www.bestpractices.dev/projects/11002)
![Language](https://img.shields.io/badge/language-Java-blue.svg)
[![version](https://img.shields.io/github/v/tag/openfacade/http-facade?label=release&color=blue)](https://github.com/openfacade/http-facade/releases)
[![codecov](https://codecov.io/gh/openfacade/http-facade/branch/main/graph/badge.svg)](https://codecov.io/gh/openfacade/http-facade)

English | [简体中文](README_CN.md)

`HTTP Facade` is a flexible Java library that provides a unified API for both HTTP clients and HTTP servers(WIP). The library supports multiple underlying implementations (e.g., OkHttp, AsyncHttpClient for clients) and provides easy configuration of HTTP requests and responses, connection timeouts, TLS settings, routing, and more.

## Why Use a Facade Library?

The `HTTP Facade` library offers a streamlined approach for integrating multiple HTTP client implementations within a single application.
By providing a unified API, it abstracts the complexities of various HTTP engines (such as `OkHttp` or `AsyncHttpClient`) while allowing easy configuration of essential features like request timeouts, TLS settings.

In many integration scenarios, applications based on HTTP libraries may need to adapt to specific requirements.
For instance, some embedded environments prioritize smaller footprint sizes, Android applications require platform-specific compatibility, and other cases may demand high performance and low latency.
In these situations, using a thin facade layer can be highly advantageous, allowing developers to meet diverse needs without rewriting core logic or managing multiple client libraries.
Instead, `HTTP Facade` enables easy switching between implementations, ensuring consistency, simplicity, and enhanced maintainability across various use cases.

## HttpClientFacade

### HttpClient Support Engines

- [**AsyncHttpClient**](https://github.com/AsyncHttpClient/async-http-client)
- **JavaHttpClient**: Java11+ built-in HTTP client
- **Java8HttpClient**: Java8 built-in HTTP client
- [**JettyHttpClient**](https://github.com/jetty/jetty.project)
- [**OkHttpClient**](https://github.com/square/okhttp)
- [**VertxHttpClient**](https://github.com/vert-x3/vertx-web)

### Installation

By default, the `HttpClientFacade` uses the built-in HTTP engine provided by the JDK. It automatically selects the appropriate implementation based on the Java version, supporting both Java 8 and Java 11+. However, if you wish to use a specific HTTP engine like `OkHttp` or `AsyncHttpClient`, you'll need to add the corresponding dependencies.

#### Maven

Add the following dependency to your `pom.xml` to include the default `http-facade`:

```xml
<dependency>
    <groupId>io.github.openfacade</groupId>
    <artifactId>http-facade</artifactId>
    <version>${http-facade.version}</version>
</dependency>
```

If you'd like to use a specific HTTP engine, you need to add additional dependencies:

- **ApacheHttpClient**:

  ```xml
  <dependency>
      <groupId>io.github.openfacade</groupId>
      <artifactId>http-facade</artifactId>
      <version>${http-facade.version}</version>
  </dependency>
  <dependency>
      <groupId>org.apache.httpcomponents.client5</groupId>
      <artifactId>httpclient5</artifactId>
      <version>${apache-http-client.version}</version>
  </dependency>
  ```

- **AsyncHttpClient**:

  ```xml
  <dependency>
      <groupId>io.github.openfacade</groupId>
      <artifactId>http-facade</artifactId>
      <version>${http-facade.version}</version>
  </dependency>
  <dependency>
      <groupId>org.asynchttpclient</groupId>
      <artifactId>async-http-client</artifactId>
      <version>${asynchttp.version}</version>
  </dependency>
  ```

- **Jetty**:

  ```xml
  <dependency>
      <groupId>io.github.openfacade</groupId>
      <artifactId>http-facade</artifactId>
      <version>${http-facade.version}</version>
  </dependency>
  <dependency>
      <groupId>org.eclipse.jetty</groupId>
      <artifactId>jetty-client</artifactId>
    <version>${jetty.version}</version>
  </dependency>
  ```

- **OkHttp**:

  ```xml
  <dependency>
      <groupId>io.github.openfacade</groupId>
      <artifactId>http-facade</artifactId>
      <version>${http-facade.version}</version>
  </dependency>
  <dependency>
      <groupId>com.squareup.okhttp3</groupId>
      <artifactId>okhttp</artifactId>
    <version>${okhttp.version}</version>
  </dependency>
  ```

- **Vertx**:

  ```xml
  <dependency>
      <groupId>io.github.openfacade</groupId>
      <artifactId>http-facade</artifactId>
      <version>${http-facade.version}</version>
  </dependency>
  <dependency>
      <groupId>io.vertx</groupId>
      <artifactId>vertx-web-client</artifactId>
    <version>${vertx.version}</version>
  </dependency>
  ```

#### Gradle

For Gradle users, add the default `http-facade` dependency:

```groovy
implementation 'io.github.openfacade:http-facade-client:$httpFacadeVersion'
```

To specify an HTTP engine, need to add dependencies:

- **ApacheHttpClient**:

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'org.apache.httpcomponents.client5:httpclient5:$apacheHttpClientVersion}'
  ```

- **AsyncHttpClient**:

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'org.asynchttpclient:async-http-client:$asynchttpVersion'
  ```

- **Jetty**:

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'org.eclipse.jetty:jetty-client:$jettyVersion'
  ```

- **OkHttp**:

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'com.squareup.okhttp3:okhttp:$okhttpVersion'
  ```

- **Vertx**:

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'io.vertx:vertx-web-client:$vertxVersion'
  ```

### Getting Started

#### 1. Create a Configuration

First, create a `HttpClientConfig` to configure timeouts and TLS settings.

```java
HttpClientConfig config = new HttpClientConfig.Builder()
    .engine(HttpClientEngine.OKHTTP) // Choose the engine (e.g., OKHTTP, ASYNC_HTTP_CLIENT, JDK)
    .timeout(Duration.ofSeconds(30)) // Set request timeout
    .connectTimeout(Duration.ofSeconds(10)) // Set connection timeout
    .build();
```

#### 2. Create a Client

Use the `HttpClientFactory` to create the desired client instance with the above configuration.

```java
HttpClient client = HttpClientFactory.createHttpClient(config);
```

#### 3. Make HTTP Requests

##### Asynchronous Requests

```java
CompletableFuture<HttpResponse> future = client.get("http://example.com", Map.of());
future.thenAccept(response -> {
    System.out.println("Status: " + response.getStatusCode());
    System.out.println("Body: " + new String(response.getBody()));
});
```

##### Synchronous Requests

```java
HttpResponse response = client.getSync("http://example.com", Map.of());
System.out.println("Status: " + response.getStatusCode());
System.out.println("Body: " + new String(response.getBody()));
```

#### 4. Close the Client

Remember to close the client when you're done to release any resources.

```java
client.close();
```
