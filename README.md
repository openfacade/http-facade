# HTTP Facade

![License](https://img.shields.io/badge/license-Apache2.0-green) ![Language](https://img.shields.io/badge/language-Java-blue.svg) [![version](https://img.shields.io/github/v/tag/openfacade/http-facade?label=release&color=blue)](https://github.com/openfacade/http-facade/releases)

`HTTP Facade` is a flexible Java library that provides a unified API for both HTTP clients and HTTP servers(WIP). The library supports multiple underlying implementations (e.g., OkHttp, AsyncHttpClient for clients) and provides easy configuration of HTTP requests and responses, connection timeouts, TLS settings, routing, and more.

## HttpClientFacade

### Features

- **Support for Multiple HTTP Engines**:
    - `AsyncHttpClient`
    - `JDK HttpClient`
    - `JDK8 HttpClient`
    - `OkHttp`

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

If you'd like to use a specific HTTP engine, you can include the following additional dependencies:

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

#### Gradle

For Gradle users, add the default `http-facade` dependency:

```groovy
implementation 'io.github.openfacade:http-facade-client:$httpFacadeVersion'
```

To specify an HTTP engine, add the following additional dependencies:

- **AsyncHttpClient**:

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'org.asynchttpclient:async-http-client:$asynchttpVersion'
   ```

- **OkHttp**:

  ```groovy
  implementation 'io.github.openfacade:http-facade-client-okhttp:$httpFacadeVersion'
  implementation 'com.squareup.okhttp3:okhttp:$okhttpVersion'
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
